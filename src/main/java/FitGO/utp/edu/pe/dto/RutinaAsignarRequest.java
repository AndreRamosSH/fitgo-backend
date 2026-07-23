package FitGO.utp.edu.pe.dto;

import java.util.List;

public class RutinaAsignarRequest {
    private List<Long> miembroIds;

    public RutinaAsignarRequest() {
    }

    public RutinaAsignarRequest(List<Long> miembroIds) {
        this.miembroIds = miembroIds;
    }

    public List<Long> getMiembroIds() {
        return miembroIds;
    }

    public void setMiembroIds(List<Long> miembroIds) {
        this.miembroIds = miembroIds;
    }
}
