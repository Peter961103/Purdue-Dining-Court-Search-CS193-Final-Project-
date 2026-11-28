import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String cachePath = "menu_cache.json";
        MenuFetcher fetcher = new MenuFetcher();
        LocalDate targetDate = LocalDate.now();

        System.out.println("Fetching Purdue dining menus for " + targetDate + "...");
        List<MenuItem> menuItems = fetcher.fetchAllMenus(targetDate);

        if (menuItems.isEmpty()) {
            System.out.println("Could not reach live API or no menus available. Loading cached menu...");
            menuItems = MenuCache.loadFromFile(cachePath);
        } else {
            MenuCache.saveToFile(cachePath, menuItems);
            System.out.println("Loaded " + menuItems.size() + " items.");
        }

        if (menuItems.isEmpty()) {
            System.out.println("No menu data available to search.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nEnter a food keyword to search (or 'quit' to exit): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equalsIgnoreCase("quit")) {
                break;
            }
            if (keyword.isEmpty()) {
                continue;
            }

            List<MenuItem> matches = menuItems.stream()
                    .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();

            if (matches.isEmpty()) {
                System.out.println("No matches found for \"" + keyword + "\".");
            } else {
                System.out.println("Found " + matches.size() + " match(es):");
                for (MenuItem item : matches) {
                    System.out.println("  " + item);
                }
            }
        }
        scanner.close();
    }
}