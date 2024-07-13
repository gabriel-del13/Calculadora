// He decidido crear esta aplicación sencilla commo mi primer proyecto de Java
// Biblioteca utilizadas:
import javafx.application.Application; // Clase base para todas las aplicaciones JavaFX
import javafx.geometry.Insets; // Clase para definir márgenes (espacios) alrededor de un nodo 
import javafx.scene.Scene; // Representa la escena de la aplicación JavaFX (interfaz, botones, campos de texto, etc.)
import javafx.scene.control.Button; // Representa un botón en la interfaz gráfica
import javafx.scene.control.TextField; // Representa un campo de texto de una sola línea
import javafx.scene.image.Image; // Clase para cargar y representar una imagen
import javafx.scene.image.ImageView; // Nodo que muestra una imagen
import javafx.scene.layout.GridPane; // Contenedor que organiza sus hijos en una cuadrícula
import javafx.stage.Stage; // Representa la ventana principal de la aplicación

public class Calculator extends Application {

    private TextField display; //Muestro de numeros y resultados en la calculadora (espacio en blanco)

    @Override
    public void start(Stage primaryStage) { //Ventana principal
        primaryStage.setTitle("Calculadora de Gabriel"); //Titulo

        display = new TextField();
        display.setEditable(false);
        display.setPrefHeight(50);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Botones
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "C", "=", "+"
        };

        int rowIndex = 1;
        int colIndex = 0;

        for (String label : buttonLabels) {
            Button button = new Button(label);
            button.setPrefSize(50, 50);
            button.setOnAction(e -> buttonClicked(label));
            grid.add(button, colIndex, rowIndex);

            colIndex++;
            if (colIndex > 3) {
                colIndex = 0;
                rowIndex++;
            }
        }

        grid.add(display, 0, 0, 4, 1);

        // Cambio #1: Añadiendo Imagen
        ImageView imageView = new ImageView();
        imageView.setImage(new Image("file:fondo.jpg"));
        imageView.setFitWidth(200); 
        imageView.setPreserveRatio(true); 
        grid.add(imageView, 4, 1,1,3); 

        Scene scene = new Scene(grid, 400, 310);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void buttonClicked(String label) { // = y c
        switch (label) {
            case "=":
                evaluate();
                break;
            case "C":
                display.clear();
                break;
            default:
                display.appendText(label);
                break;
        }
    }

    private void evaluate() { //Resultado
        try {
            String result = String.valueOf(eval(display.getText()));
            display.setText(result);
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    // Metodo de evaluacion simple (sumas, restas, multi y division)  //pos: posicion actual en la cadena de expression / ch: caracter actual en la posicion pos
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }
    // Run de JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}
