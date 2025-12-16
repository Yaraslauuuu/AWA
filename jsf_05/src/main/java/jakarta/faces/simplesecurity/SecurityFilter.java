package jakarta.faces.simplesecurity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SecurityFilter implements Filter {
	private String noPermitionPage;
	private HashSet<String> publicResources = new HashSet<String>();
	private Hashtable<String, HashSet<String>> permitions = new Hashtable<String, HashSet<String>>();
	private boolean invlidateOnHostChange = false;
	private boolean invlidateOnAddrChange = false;
	private boolean invlidateOnPortChange = false;

	private static final String FACES_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

	@Override
	public void init(FilterConfig config) throws ServletException {
		String perms = config.getInitParameter("permissions");
		if (perms != null) {
			String[] list = perms.split(";");
			for (String perm : list) {
				perm = perm.trim();
				if (!perm.isEmpty()) {
					String[] row = perm.split(":");
					String resource = row[0].trim();
					if (!resource.isEmpty()) {
						HashSet<String> roles = new HashSet<String>();
						if (row.length > 1) {
							String[] rawroles = row[1].split(",");
							for (String role : rawroles) {
								role = role.trim();
								if ("PUBLIC".equals(role)) {
									role = "";
								}
								if (!role.isEmpty()) {
									roles.add(role);
								}
							}
						}
						if (roles.size() == 0) {
							publicResources.add(resource);
						} else {
							permitions.put(resource, roles);
						}
					}
				}
			}
		}
		noPermitionPage = config.getInitParameter("noPermitionPage");
		if (noPermitionPage == null) {
			noPermitionPage = "/login.xhtml";
		}
		publicResources.add(noPermitionPage);
		String param = config.getInitParameter("invalidateOnRemoteHostChange");
		if (param != null && "TRUE".equals(param.toUpperCase())) {
			invlidateOnHostChange = true;
		}
		param = config.getInitParameter("invalidateOnRemoteAddressChange");
		if (param != null && "TRUE".equals(param.toUpperCase())) {
			invlidateOnAddrChange = true;
		}
		param = config.getInitParameter("invalidateOnRemotePortChange");
		if (param != null && "TRUE".equals(param.toUpperCase())) {
			invlidateOnPortChange = true;
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();

		RemoteClient client = RemoteClient.load(session);

		boolean pass = false;

		String path = request.getServletPath();
		for (String resource : publicResources) {
			if (path.startsWith(resource)) {
				pass = true;
			}
		}

		if (!pass && client != null) {
			int idx = path.length();
			do {
				path = path.substring(0, idx);
				if (path.isEmpty()) {
					path = "/";
				}
				if (permitions.containsKey(path)) {
					HashSet<String> roles = permitions.get(path);
					if (client.isInOneRole(roles)) {
						pass = true;
					}
					break;
				}
				idx = path.lastIndexOf("/");
			} while (path.length() > 1);

			if (invlidateOnHostChange && client.getRemoteHost() != null) {
				if (!request.getRemoteHost().equals(client.getRemoteHost())) {
					session.invalidate();
				}
			}
			if (invlidateOnAddrChange && client.getRemoteAddr() != null) {
				if (!request.getRemoteAddr().equals(client.getRemoteAddr())) {
					session.invalidate();
				}
			}
			if (invlidateOnPortChange) {
				if (request.getRemotePort() != client.getRemotePort()) {
					session.invalidate();
				}
			}

		}

		if (!pass) {
			if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
				res.setContentType("text/xml");
				res.setCharacterEncoding("UTF-8");
				res.getWriter().printf(FACES_REDIRECT_XML, request.getContextPath() + "/");
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				request.getServletContext().getRequestDispatcher(noPermitionPage).forward(request, response);
			}

		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void destroy() {
	}

}

