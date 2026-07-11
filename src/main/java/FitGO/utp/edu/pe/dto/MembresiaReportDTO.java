package FitGO.utp.edu.pe.dto;

import java.util.List;

public class MembresiaReportDTO {
    private Long activas;
    private Long porVencer;
    private Long vencidas;
    private String planMasPopular;
    private List<DistribucionPlanDTO> distribucionPlan;
    private List<ProximaVencerDTO> proximasVencer;

    public MembresiaReportDTO() {}

    public MembresiaReportDTO(Long activas, Long porVencer, Long vencidas, String planMasPopular,
                              List<DistribucionPlanDTO> distribucionPlan, List<ProximaVencerDTO> proximasVencer) {
        this.activas = activas;
        this.porVencer = porVencer;
        this.vencidas = vencidas;
        this.planMasPopular = planMasPopular;
        this.distribucionPlan = distribucionPlan;
        this.proximasVencer = proximasVencer;
    }

    public Long getActivas() {
        return activas;
    }

    public void setActivas(Long activas) {
        this.activas = activas;
    }

    public Long getPorVencer() {
        return porVencer;
    }

    public void setPorVencer(Long porVencer) {
        this.porVencer = porVencer;
    }

    public Long getVencidas() {
        return vencidas;
    }

    public void setVencidas(Long vencidas) {
        this.vencidas = vencidas;
    }

    public String getPlanMasPopular() {
        return planMasPopular;
    }

    public void setPlanMasPopular(String planMasPopular) {
        this.planMasPopular = planMasPopular;
    }

    public List<DistribucionPlanDTO> getDistribucionPlan() {
        return distribucionPlan;
    }

    public void setDistribucionPlan(List<DistribucionPlanDTO> distribucionPlan) {
        this.distribucionPlan = distribucionPlan;
    }

    public List<ProximaVencerDTO> getProximasVencer() {
        return proximasVencer;
    }

    public void setProximasVencer(List<ProximaVencerDTO> proximasVencer) {
        this.proximasVencer = proximasVencer;
    }

    public static class DistribucionPlanDTO {
        private String nombre;
        private Long miembros;
        private Double porcentaje;
        private String color;

        public DistribucionPlanDTO() {}

        public DistribucionPlanDTO(String nombre, Long miembros, Double porcentaje, String color) {
            this.nombre = nombre;
            this.miembros = miembros;
            this.porcentaje = porcentaje;
            this.color = color;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Long getMiembros() {
            return miembros;
        }

        public void setMiembros(Long miembros) {
            this.miembros = miembros;
        }

        public Double getPorcentaje() {
            return porcentaje;
        }

        public void setPorcentaje(Double porcentaje) {
            this.porcentaje = porcentaje;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class ProximaVencerDTO {
        private String miembro;
        private String plan;
        private String vence;
        private String dias;
        private String estado;

        public ProximaVencerDTO() {}

        public ProximaVencerDTO(String miembro, String plan, String vence, String dias, String estado) {
            this.miembro = miembro;
            this.plan = plan;
            this.vence = vence;
            this.dias = dias;
            this.estado = estado;
        }

        public String getMiembro() {
            return miembro;
        }

        public void setMiembro(String miembro) {
            this.miembro = miembro;
        }

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getVence() {
            return vence;
        }

        public void setVence(String vence) {
            this.vence = vence;
        }

        public String getDias() {
            return dias;
        }

        public void setDias(String dias) {
            this.dias = dias;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}
