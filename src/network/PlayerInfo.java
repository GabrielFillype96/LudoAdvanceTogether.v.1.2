package network;

public class PlayerInfo {
    private int id;
    private String name;
    private int colorIndex; // 0 = Azul, 1 = Roxo, 2 = Rosa, 3 = Amarelo
    private boolean isCPU;

    public PlayerInfo(int id, String name, int colorIndex, boolean isCPU) {
        this.id = id;
        this.name = name;
        this.colorIndex = colorIndex;
        this.isCPU = isCPU;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getColorIndex() { return colorIndex; }
    public void setColorIndex(int colorIndex) { this.colorIndex = colorIndex; }

    public boolean isCPU() { return isCPU; }
    public void setCPU(boolean isCPU) { this.isCPU = isCPU; }
}