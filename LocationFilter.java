package smartcampus.model;

public class LocationFilter {
    private boolean showBuildings;
    private boolean showCafes;
    private boolean showLibraries;
    private boolean showSports;
    private boolean showParking;

    // Getters and Setters
    public boolean isShowBuildings() {
        return showBuildings;
    }

    public void setShowBuildings(boolean showBuildings) {
        this.showBuildings = showBuildings;
    }

    public boolean isShowCafes() {
        return showCafes;
    }

    public void setShowCafes(boolean showCafes) {
        this.showCafes = showCafes;
    }

    public boolean isShowLibraries() {
        return showLibraries;
    }

    public void setShowLibraries(boolean showLibraries) {
        this.showLibraries = showLibraries;
    }

    public boolean isShowSports() {
        return showSports;
    }

    public void setShowSports(boolean showSports) {
        this.showSports = showSports;
    }

    public boolean isShowParking() {
        return showParking;
    }

    public void setshowParking(boolean showParking) {
        this.showParking = showParking;
    }
}
