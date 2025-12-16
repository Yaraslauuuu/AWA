package com.jsfcourse.calc;

import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Named
@ViewScoped
public class CalcBB implements Serializable {
    private Double x;
    private Double y;
    private Double result;

    // Resource injected
    @Inject
    @ManagedProperty("#{txtCalcErr}")
    private ResourceBundle txtCalcErr;

    // Resource injected
    @Inject
    @ManagedProperty("#{txtCalc}")
    private ResourceBundle txtCalc;

    @Inject
    FacesContext ctx;

    // Resource loaded "manually"
    // (here after initialization in @PostConstruct method)
    // (locale explicitly given - here based on the view )
    // @PostConstruct
    // public void postConstruct() {
    // // loading resource (notice the full "file" name)
    // txtCalc = ResourceBundle.getBundle("resources.textCalc",
    // ctx.getViewRoot().getLocale());
    // txtCalcErr = ResourceBundle.getBundle("resources.textCalcErr",
    // ctx.getViewRoot().getLocale());
    // }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getResult() {
        return result;
    }

    public String calc() {
        result = x + y;

        HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
        req.setAttribute("wynik", result);
        
        HttpSession session = (HttpSession) ctx.getExternalContext().getSession(true);
        session.setAttribute("ostatniWynik", result);

        ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, txtCalcErr.getString("calcComputationOkInfo"), null));
        ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, txtCalc.getString("result") + ": " + result, null));

        return null;
    }

    public String zapiszWynikWSesji() {
        HttpSession session = (HttpSession) ctx.getExternalContext().getSession(true);
        session.setAttribute("wynikSesji", result);
        return null;
    }

    public String ustawCiasteczka() {
        HttpServletResponse resp = (HttpServletResponse) ctx.getExternalContext().getResponse();
        
        Cookie c1 = new Cookie("wynik", String.valueOf(result));
        c1.setMaxAge(60 * 60 * 24);
        
        Cookie c2 = new Cookie("x", String.valueOf(x));
        c2.setMaxAge(60 * 60 * 24);
        
        Cookie c3 = new Cookie("y", String.valueOf(y));
        c3.setMaxAge(60 * 60 * 24);
        
        resp.addCookie(c1);
        resp.addCookie(c2);
        resp.addCookie(c3);
        
        return null;
    }

    public String pobierzZSesji() {
        HttpSession session = (HttpSession) ctx.getExternalContext().getSession(true);
        Object ostatniWynik = session.getAttribute("ostatniWynik");
        if (ostatniWynik != null) {
            result = (Double) ostatniWynik;
        }
        return null;
    }

}
