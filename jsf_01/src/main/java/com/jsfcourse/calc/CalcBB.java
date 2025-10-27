package com.jsfcourse.calc;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@RequestScoped
public class CalcBB {
    private String x; 
    private String y; 
    private String z; 
    private Double result; 

    public String getX() { return x; }
    public void setX(String x) { this.x = x; }

    public String getY() { return y; }
    public void setY(String y) { this.y = y; }

    public String getZ() { return z; }
    public void setZ(String z) { this.z = z; }

    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }

    public boolean doTheMath() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            double principal = Double.parseDouble(this.x);
            double annualRate = Double.parseDouble(this.y) / 100;
            int months = Integer.parseInt(this.z);
            double monthlyRate = annualRate / 12;

            if (principal <= 0 || months <= 0) throw new NumberFormatException();

            if (monthlyRate == 0) {
                result = principal / months;
            } else {
                result = principal * monthlyRate * Math.pow(1 + monthlyRate, months)
                        / (Math.pow(1 + monthlyRate, months) - 1);
            }

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operacja wykonana poprawnie", null));
            return true;
        } catch (Exception e) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas przetwarzania parametrów", null));
            return false;
        }
    }

    public String calc() {
        if (doTheMath()) {
            return "showresult";
        }
        return null;
    }

    public String calc_AJAX() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (doTheMath()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Miesięczna rata: " + String.format("%.2f", result), null));
        }
        return null;
    }

    public String info() {
        return "info";
    }
}
