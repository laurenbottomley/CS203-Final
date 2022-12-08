import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Bouncing extends Pane {
    final int WINDOW_X = 640;
    final int WINDOW_Y = 320;
    Timeline loopTimeline;
    int ballCount;

    public Bouncing() {
        ballCount = 50;
        loopTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            throwThemAll();
        }));
        loopTimeline.setCycleCount(Timeline.INDEFINITE);

    };

    public void start() {
        load();
        loopTimeline.play();
    }

    private void load() {
        getChildren().clear();
        Ball ball = new Ball(Math.random() * ((Region) getParent()).getWidth(),
                Math.random() * ((Region) getParent()).getHeight(), 5, Color.RED);

        for (int i = 0; i < ballCount; i++) {
            getChildren().add(new Ball(Math.random() * ((Region) getParent()).getWidth(),
                    Math.random() * ((Region) getParent()).getHeight(), (Math.random() * 24) + 1,

                    Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 155), (int) (Math.random() * 255))));

        }
        getChildren().add(ball);
    }

    public void toggleAll() {
        for (Node n : getChildren()) {
            if (n instanceof Ball) {
                ((Ball) n).toggle();
            }
        }
    }

    public void throwThemAll() {
        for (Node n : getChildren()) {
            if (n instanceof Ball) {
                ((Ball) n).throwIt();
            }
        }
    }

    public void stop() {
        loopTimeline.stop();
        toggleAll();
    }

}
