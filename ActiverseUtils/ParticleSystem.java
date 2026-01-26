package ActiverseUtils;

import ActiverseEngine.Actor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ParticleSystem - System for creating and managing visual particle effects
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class ParticleSystem extends Actor {
    private List<Particle> particles;
    private Random random;
    
    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
        setStatic(true); // Particle system doesn't move
    }
    
    /**
     * Emits particles from a position
     *
     * @param x X position
     * @param y Y position
     * @param count Number of particles
     * @param color Particle color
     * @param lifetime Lifetime in milliseconds
     */
    public void emit(int x, int y, int count, Color color, long lifetime) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = 2 + random.nextDouble() * 3;
            
            Particle p = new Particle(
                x, y,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed,
                color,
                lifetime,
                3 + random.nextInt(5) // Size 3-7
            );
            particles.add(p);
        }
    }
    
    /**
     * Emits particles in a specific direction
     *
     * @param x X position
     * @param y Y position
     * @param count Number of particles
     * @param color Particle color
     * @param lifetime Lifetime in milliseconds
     * @param angle Direction angle in radians
     * @param spread Spread angle in radians
     */
    public void emitDirectional(int x, int y, int count, Color color, long lifetime, double angle, double spread) {
        for (int i = 0; i < count; i++) {
            double particleAngle = angle + (random.nextDouble() - 0.5) * spread;
            double speed = 2 + random.nextDouble() * 3;
            
            Particle p = new Particle(
                x, y,
                Math.cos(particleAngle) * speed,
                Math.sin(particleAngle) * speed,
                color,
                lifetime,
                3 + random.nextInt(5)
            );
            particles.add(p);
        }
    }
    
    @Override
    public void act() {
        // Update and remove dead particles
        List<Particle> toRemove = new ArrayList<>();
        for (Particle p : particles) {
            p.update();
            if (p.isDead()) {
                toRemove.add(p);
            }
        }
        particles.removeAll(toRemove);
    }
    
    @Override
    public void paint(Graphics g) {
        for (Particle p : particles) {
            p.render(g);
        }
    }
    
    /**
     * Individual particle
     */
    private static class Particle {
        private double x, y;
        private double vx, vy;
        private Color color;
        private long createdTime;
        private long lifetime;
        private int size;
        private double gravity = 0.1;
        
        public Particle(double x, double y, double vx, double vy, Color color, long lifetime, int size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.lifetime = lifetime;
            this.size = size;
            this.createdTime = System.currentTimeMillis();
        }
        
        public void update() {
            x += vx;
            y += vy;
            vy += gravity; // Apply gravity
            vx *= 0.98; // Friction
            vy *= 0.98;
        }
        
        public boolean isDead() {
            return System.currentTimeMillis() - createdTime > lifetime;
        }
        
        public void render(Graphics g) {
            // Fade out over time
            long age = System.currentTimeMillis() - createdTime;
            float alpha = 1.0f - ((float)age / lifetime);
            alpha = Math.max(0, Math.min(1, alpha));
            
            Color fadeColor = new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int)(255 * alpha)
            );
            
            g.setColor(fadeColor);
            g.fillOval((int)x, (int)y, size, size);
        }
    }
}
