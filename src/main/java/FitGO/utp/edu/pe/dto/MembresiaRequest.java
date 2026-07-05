package FitGO.utp.edu.pe.dto;

public class MembresiaRequest {

    private Long usuarioId;
    private Long planId;

    public MembresiaRequest() {
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
