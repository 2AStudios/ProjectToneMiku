import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }
        catch (ClassNotFoundException e) {
            // handle exception
        }
        catch (InstantiationException e) {
            // handle exception
        }
        catch (IllegalAccessException e) {
            // handle exception
        }
        //System.out.println("Hello World!");
        MainWindow application = new MainWindow();
    }

    public class Tuple<X, Y> {
        public X x; //Animation Set
        public Y y; //Animation Frame
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Tuple)) {
                return false;
            } else {
                Tuple that = (Tuple)obj;
                return this.x.equals(that.x) &&
                        this.y.equals(that.y);
            }
        }

        public int hashCode() {
            int hash = this.x.hashCode();
            hash = hash * 31 + this.y.hashCode();
            return hash;
        }
    }
}
