public class Player {
    private final String name;
    private final String position;
    private final int height;
    private final int weight;
    private final double age;

    public Player(String name, String position, int height, int weight, double age) {
        this.name = name;
        this.position = position;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public double getAge() {
        return age;
    }
}
