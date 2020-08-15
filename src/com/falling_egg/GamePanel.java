package com.falling_egg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel {
    int xEgg, yEgg;
    int xBas, yBas;
    Random random = new Random();
    JLabel currentLivesLabel = new JLabel("Lives : 3");
    JLabel timeLabel = new JLabel("Time : 0");
    JLabel pointsLabel = new JLabel("Points : 0");
    JLabel userLabel;
    int secondsElapsed;
    int currentFallingSpeed = new EggsLogic().getFallingSpeed(0);
    int currentPoints = 0;
    boolean isGameOver = false;
    int remainingLives = 3;
    boolean shouldEggFall = true;
    Egg currentEgg;

    GamePanel(String username) {
        setLayout(null);
        setFocusable(true);
        xBas = 450;
        yBas = 600;
        xEgg = random.nextInt(1000);
        yEgg = 0;
        secondsElapsed = 0;
        updateCurrentEgg();
        userLabel = new JLabel("UserName : " + username);
        timeLabel.setBounds(20, 10, 100, 20);
        pointsLabel.setBounds(110, 10, 100, 20);
        currentLivesLabel.setBounds(210, 10, 100, 20);
        userLabel.setBounds(310, 10, 300, 20);
        add(timeLabel);
        add(pointsLabel);
        add(currentLivesLabel);
        add(userLabel);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == e.VK_LEFT & xBas > 10) {
                    xBas -= 20;
                    repaint();
                }
                if (e.getKeyCode() == e.VK_RIGHT & xBas < 1000) {
                    xBas += 20;
                    repaint();
                }
            }
        });
    }

    void updateTime() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (remainingLives > 0) {
                shouldEggFall = true;
                secondsElapsed += 1;
                timeLabel.setText("Time : " + secondsElapsed);
            } else {
                isGameOver = true;
                executor.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    void updateCurrentEgg() {
        currentEgg = new EggsLogic().getEgg();
    }

    void makeEggFall() {
        if (yEgg >= 650) {
            shouldEggFall = false;
            yEgg = 0;
            xEgg = random.nextInt(1000);
            updateCurrentEgg();
            currentFallingSpeed = new EggsLogic().getFallingSpeed(secondsElapsed);
        } else {
            if (shouldEggFall) yEgg += currentFallingSpeed;
//             yEgg += 1;
        }
    }


    void processCollision() {
        Rectangle egg = new Rectangle(xEgg, yEgg, 45, 65);
        Rectangle basket = new Rectangle(xBas, yBas, 100, 65);
        if (basket.intersects(egg)) {
            currentFallingSpeed = new EggsLogic().getFallingSpeed(secondsElapsed);
            shouldEggFall = false;
            remainingLives += currentEgg.points == -1 ? -1 : 0;
            remainingLives += currentEgg.points == 100 ? 1 : 0;
            currentLivesLabel.setText("Lives : " + remainingLives);
            if (currentEgg.points != -1 && currentEgg.points != 100) {
                currentPoints += currentEgg.points;
            }
            pointsLabel.setText("Points : " + currentPoints);
            yEgg = 0;
            updateCurrentEgg();
            xEgg = random.nextInt(1000);
        }
    }


    public void paintComponent(Graphics g) {
        if (secondsElapsed == 0) {
            updateTime();
        }
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (!isGameOver) {
            setFocusable(true);
            grabFocus();
            makeEggFall();
            processCollision();
            if (!currentEgg.isNull && shouldEggFall) {
                g2d.setPaint(currentEgg.color);
                g2d.fillOval(xEgg, yEgg, 45, 65);
            }
            g2d.setPaint(Color.ORANGE);
            g2d.fillRect(xBas, yBas, 100, 65);
        } else {
            g2d.drawString("Game Over! Score : " + currentPoints, 500, 300);
        }

        repaint();
    }
}


class Egg {
    int points;
    Color color;
    boolean isNull;

    Egg(int points, Color color, boolean isNull) {
        this.points = points;
        this.color = color;
        this.isNull = isNull;
    }
}

class EggsLogic {
    Map<Integer, Map> probabilityMap = new HashMap<>();
    int[] probabilityArray = new int[]{80, 60, 40, 20, 10, 0};

    public Egg getEgg() {
        probabilityMap.put(80, getMappedValue(Color.CYAN, 30));// 30 points
        probabilityMap.put(60, getMappedValue(Color.MAGENTA, 20));//20 points
        probabilityMap.put(40, getMappedValue(Color.ORANGE, 10));//10 points
        probabilityMap.put(20, getMappedValue(Color.RED, -1));//death
        probabilityMap.put(10, getMappedValue(Color.GREEN, 100));//life
        probabilityMap.put(0, null);//null
        int event = new Random().nextInt(100);
        Egg toReturn = null;
        for (int i = 0; i < probabilityArray.length; i++) {
            if (event >= probabilityArray[i]) {
                Map resultMap = probabilityMap.get(probabilityArray[i]);
                if (resultMap == null) {
                    toReturn = new Egg(0, Color.WHITE, true);
                } else {
                    toReturn = new Egg((int) resultMap.get("points"), (Color) resultMap.get("color"), false);
                }
                break;
            }
        }

        return toReturn;
    }

    public static void main(String[] args) {
        Egg ne = new EggsLogic().getEgg();
        System.out.print("points : " + ne.points + " color : " + ne.color + " isnull : " + ne.isNull);
    }

    private Map getMappedValue(Color color, int points) {
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("points", points);
        toReturn.put("color", color);
        return toReturn;
    }

    public int getFallingSpeed(int secondsElapsed) {
        System.out.print("sec : " + secondsElapsed + "\n");
        int minutesElapsed = secondsElapsed / 60;
        System.out.print("min : " + minutesElapsed + "\n");
        int multiplier = 1 + new Random().nextInt(5);
        if (minutesElapsed == 0) {
            return multiplier;
        } else {
            return multiplier * minutesElapsed;
        }
    }

}
