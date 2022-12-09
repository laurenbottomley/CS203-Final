import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Ball extends Circle {
    private double xVel = 0;
    private double yVel = 0;
    private double oldX = 0;
    private double oldY = 0;
    final double BOUNCE_DAMPER = .7;
    final double ROLL_DAMPER = .96;
    final double GRAVITY = .5;
    final private int FRAMERATE = 30;
    Timeline timeline;

    public Ball(double x, double y, double radius, Paint fill) {
        super(x, y, radius, fill);

        everythingElse();

    }

    private void everythingElse() {
        // .015625
        timeline = new Timeline(FRAMERATE, new KeyFrame(Duration.seconds(.015625), e -> {

            fall();

            wallBounce();

            stopRoll();

            floorBounce();

            slowYourRoll();

            // move
            setCenterX(getCenterX() + xVel);
            setCenterY(getCenterY() + yVel);

            // stay on screen-ish
            setCenterY(
                    getCenterY() > ((Region) getParent()).getHeight() - getRadius()
                            ? ((Region) getParent()).getHeight() - getRadius()
                            : getCenterY());
            setCenterX(
                    getCenterX() > ((Region) getParent()).getWidth() - getRadius()
                            ? getParent().getScene().getWidth() - getRadius()
                            : getCenterX());

        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        setOnMouseDragged(e -> {
            timeline.stop();
            setCenterX(e.getX());
            setCenterY(e.getY());

            yVel = getCenterY() - oldY;

            oldY = getCenterY();

            xVel = getCenterX() - oldX;

            oldX = getCenterX();

        });
        setOnMouseReleased(e -> {
            timeline.play();
        });

    }

    private void fall() {
        if (getCenterY() < ((Region) getParent()).getHeight() - getRadius()) {
            yVel += GRAVITY;
        }
    }

    private void wallBounce() {
        if ((getCenterX() <= getRadius() && Math.signum(xVel) == -1)
                || (getCenterX() >= ((Region) getParent()).getWidth() - getRadius()
                        && Math.signum(xVel) == 1)) {
            xVel = (-(xVel * BOUNCE_DAMPER));
        }
    }

    private void stopRoll() {
        if (Math.round(xVel) == 0) {
            xVel = 0;
        }
    }

    private void slowYourRoll() {
        if (yVel == 0) {
            xVel *= ROLL_DAMPER;
        }
    }

    private void floorBounce() {
        if (getCenterY() >= ((Region) getParent()).getHeight() - getRadius()
                && Math.signum(yVel) == 1) {
            yVel = (-(yVel * BOUNCE_DAMPER));
            if ((Math.abs(yVel)) < 1.5) {
                yVel = 0;
            }

        }
    }

    public void toggle() {
        if (timeline.getStatus().equals(Status.PAUSED) || timeline.getStatus().equals(Status.STOPPED)) {
            timeline.play();
        } else
            timeline.stop();

    }

    public void throwIt() {
        xVel = Math.random() * 20 - 10;
        yVel = Math.random() * 80 - 20;
    }
}
