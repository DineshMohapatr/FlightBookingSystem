public class Passenger {
    private String name;
    private int age;
    private String phone;

    public Passenger(String name, int age, String phone) {
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return name + " (Age: " + age + ", Ph: " + phone + ")";
    }
}
