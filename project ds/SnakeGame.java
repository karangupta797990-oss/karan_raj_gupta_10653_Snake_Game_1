
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class SnakeGame {

    static final int SIZE = 10;

    static ArrayDeque<Integer> snake = new ArrayDeque<>();
    static HashSet<Integer> body = new HashSet<>();

    static char[][] grid = new char[SIZE][SIZE];

    static int score = 0;

    static int foodRow = 5;
    static int foodCol = 7;

    // Initial direction: Right
    static int dx = 0;
    static int dy = 1;

    static boolean gameOver = false;

    static Random random = new Random();

    // Convert row and column into a unique key
    static int getKey(int row, int col) {
        return row * SIZE + col;
    }

    // Initialize game
    static void initializeGame() {

        snake.clear();
        body.clear();

        score = 0;
        gameOver = false;

        dx = 0;
        dy = 1;

        // Starting position (5,5)
        int start = getKey(5, 5);

        snake.addFirst(start);
        body.add(start);

        foodRow = 5;
        foodCol = 7;

        drawGrid();
    }

    // Create and display 2D grid
    static void drawGrid() {

        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(grid[i], '.');
        }

        // Place food
        grid[foodRow][foodCol] = 'F';

        // Draw snake
        for (int position : snake) {

            int row = position / SIZE;
            int col = position % SIZE;

            grid[row][col] = 'S';
        }

        // Mark snake head
        int head = snake.peekFirst();

        int headRow = head / SIZE;
        int headCol = head % SIZE;

        grid[headRow][headCol] = 'H';

        System.out.println("\n---------- SNAKE GAME ----------");

        for (int i = 0; i < SIZE; i++) {

            for (int j = 0; j < SIZE; j++) {

                System.out.print(grid[i][j] + " ");

            }

            System.out.println();
        }

        System.out.println("Score: " + score);
        System.out.println("Length: " + snake.size());

        System.out.println("--------------------------------");
    }

    // Change direction
    static void changeDirection(char key) {

        int newDx = dx;
        int newDy = dy;

        switch (key) {

            case 'W':
                newDx = -1;
                newDy = 0;
                break;

            case 'S':
                newDx = 1;
                newDy = 0;
                break;

            case 'A':
                newDx = 0;
                newDy = -1;
                break;

            case 'D':
                newDx = 0;
                newDy = 1;
                break;

            default:
                System.out.println("Invalid key. Use W/A/S/D.");
                return;
        }

        // Prevent direct reverse movement
        if (newDx == -dx && newDy == -dy) {

            System.out.println("Reverse direction not allowed.");
            return;
        }

        dx = newDx;
        dy = newDy;

    }

    // Move snake
    static void moveSnake() {

        int head = snake.peekFirst();

        int row = head / SIZE;
        int col = head % SIZE;

        int newRow = row + dx;
        int newCol = col + dy;

        // Wall collision
        if (newRow < 0 || newRow >= SIZE ||
            newCol < 0 || newCol >= SIZE) {

            System.out.println("Wall collision. Game Over.");
            gameOver = true;
            return;
        }

        int newHead = getKey(newRow, newCol);

        // Check food
        boolean eatingFood =
                newRow == foodRow && newCol == foodCol;

        // Remove tail if food is not eaten
        if (!eatingFood) {

            int tail = snake.removeLast();

            body.remove(tail);
        }

        // Self collision
        if (body.contains(newHead)) {

            System.out.println("Self collision. Game Over.");
            gameOver = true;
            return;
        }

        // Add new head
        snake.addFirst(newHead);

        body.add(newHead);

        // Increase score and length
        if (eatingFood) {

            score += 10;

            System.out.println("Food eaten! +10 points");

            generateFood();
        }

        drawGrid();
    }

    // Generate food at an empty cell
    static void generateFood() {

        if (body.size() == SIZE * SIZE) {
            gameOver = true;
            System.out.println("You won! Grid is full.");
            return;
        }

        do {

            foodRow = random.nextInt(SIZE);
            foodCol = random.nextInt(SIZE);

        } while (body.contains(getKey(foodRow, foodCol)));

    }

    // Save score in file
    static void saveScore() {

        try {

            Files.write(
                Paths.get("scores.txt"),
                (score + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );

            System.out.println("Score saved successfully.");

        } catch (IOException e) {

            System.out.println("Error saving score.");

        }
    }

    // Display top 5 leaderboard
    static void showLeaderboard() {

        ArrayList<Integer> scores = new ArrayList<>();

        try {

            File file = new File("scores.txt");

            if (!file.exists()) {
                System.out.println("No previous scores found.");
                return;
            }

            Scanner reader = new Scanner(file);

            while (reader.hasNextInt()) {

                scores.add(reader.nextInt());

            }

            reader.close();

        } catch (Exception e) {

            System.out.println("Error reading scores.");
            return;
        }

        // Bubble Sort - Descending order
        for (int i = 0; i < scores.size() - 1; i++) {

            for (int j = 0; j < scores.size() - i - 1; j++) {

                if (scores.get(j) < scores.get(j + 1)) {

                    int temp = scores.get(j);

                    scores.set(j, scores.get(j + 1));

                    scores.set(j + 1, temp);
                }
            }
        }

        System.out.println("\n========== LEADERBOARD ==========");

        int limit = Math.min(5, scores.size());

        for (int i = 0; i < limit; i++) {

            System.out.println(
                (i + 1) + ". " + scores.get(i)
            );

        }

        System.out.println("=================================");
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        initializeGame();

        System.out.println("\nWelcome to Snake Game!");

        System.out.println("Controls:");
        System.out.println("W = Up");
        System.out.println("A = Left");
        System.out.println("S = Down");
        System.out.println("D = Right");
        System.out.println("Q = Quit");

        System.out.println("\nEnter moves separated by spaces.");
        System.out.println("Example: D D S A");

        while (!gameOver) {

            System.out.print("\nEnter move: ");

            String input = sc.nextLine();

            String[] moves = input.toUpperCase().split("[,\\s]+");

            for (String move : moves) {

                if (gameOver) {
                    break;
                }

                if (move.equals("Q")) {

                    gameOver = true;
                    break;
                }

                if (move.length() != 1) {

                    System.out.println("Invalid key. Use W/A/S/D.");
                    continue;
                }

                char key = move.charAt(0);

                if (key != 'W' && key != 'A' &&
                    key != 'S' && key != 'D') {

                    System.out.println("Invalid key. Use W/A/S/D.");
                    continue;
                }

                changeDirection(key);

                moveSnake();

            }
        }

        System.out.println("\n========== GAME OVER ==========");

        System.out.println("Final Score: " + score);

        System.out.println("Final Length: " + snake.size());

        saveScore();

        showLeaderboard();

        sc.close();
    }
}
