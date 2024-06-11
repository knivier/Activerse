public class MyWorld extends World {
    public MyWorld() {
        super(600, 400, 1);
        addObject(new MyActor(), 100, 100);
    }
}
