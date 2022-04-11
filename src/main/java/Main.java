import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static String basePath = "/home/drakko/Projects/Java/SaleforceTest";
    static String camuflatePath = "/root";
    static int minLengthName = 0;
    static int maxLengthName = 100;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String option = "";
        String[] commands = null;
        do {
            System.out.print("Option:");
            option = input.nextLine().trim();
            commands = option.split(" ");
            switch (commands[0].toLowerCase().trim()) {
                case "pwd":
                    printMessage(currentPath());
                    break;
                case "ls":
                    listDirectory(commands.length >= 2 ? commands[1].trim() : "");
                    break;
                case "mkdir":
                    if (commands.length >= 2) makeDirectory(commands[1].trim());
                    break;
                case "cd":
                    if (commands.length >= 2) changeDirectory(commands[1].trim());
                    break;
                case "touch":
                    if (commands.length >= 2) touchFile(commands[1].trim());
                    break;
                case "quit":
                    System.exit(0);
                    break;
                default:
                    printMessage("Unrecognized command");
                    break;
            }
        } while (!Objects.equals(commands[0], "quit"));
    }

    private static void listDirectory(String recursive) {
        File file = new File(basePath);
        try {
            if ("-r".equals(recursive)) {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String fantasyPath = file.toAbsolutePath().toString().replace(Paths.get(System.getProperty("user.dir")).toString(), camuflatePath);
                        System.out.println(fantasyPath);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        String fantasyPath = dir.toAbsolutePath().toString().replace(Paths.get(System.getProperty("user.dir")).toString(), camuflatePath);
                        System.out.println(fantasyPath);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                Files.walkFileTree(file.toPath(), Collections.emptySet(), 1, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String fantasyPath = file.toAbsolutePath().toString().replace(Paths.get(System.getProperty("user.dir")).toString(), camuflatePath);
                        System.out.println(fantasyPath);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            printMessage(e.getMessage());
        }

    }

    private static void touchFile(String fileName) {
        try {
            Path path = Paths.get(basePath + File.separator + fileName);
            if (!isValidValue(fileName)) throw new IOException("Invalid file name");
            if (exist(path)) throw new FileAlreadyExistsException("File already exists");
            Files.createFile(path);
        } catch (IOException e) {
            printMessage(e.getMessage());
        }
    }

    private static String currentPath() {
        return Paths.get(System.getProperty("user.dir")).toString().equals(basePath) ? camuflatePath :
                basePath.replace(Paths.get(System.getProperty("user.dir")).toString(), camuflatePath);
    }

    private static void printMessage(String msj) {
        System.out.println(msj);
    }

    private static void makeDirectory(String dirName) {
        try {
            Path path = Paths.get(basePath + File.separator + dirName);
            if (!isValidValue(dirName)) throw new IOException("Invalid directory name");
            if (exist(path)) throw new FileAlreadyExistsException("Directory already exists");
            Files.createDirectory(path);
        } catch (IOException e) {
            printMessage(e.getMessage());
        }
    }

    private static void changeDirectory(String dirName) {
        String oldPath = basePath;
        Path path = null;
        try {
            if (!isValidValue(dirName)) throw new IOException("Invalid directory name");
            if ("..".equals(dirName)) {
                basePath = basePath.replace(basePath.substring(basePath.lastIndexOf("/")), "");
                if (!basePath.contains(System.getProperty("user.dir"))) basePath = oldPath;
                path = Paths.get(basePath);
            } else if ("\\".equals(dirName)) {
                basePath = Paths.get(System.getProperty("user.dir")).toString();
                path = Paths.get(basePath);
            } else {
                basePath = basePath + "/" + dirName;
                path = Paths.get(basePath);
            }
            if (!exist(path)) {
                basePath = oldPath;
                throw new FileAlreadyExistsException("Directory not exists");
            }
        } catch (IOException e) {
            printMessage(e.getMessage());
        }

    }

    private static boolean exist(Path path) {
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    private static boolean isValidValue(@NotNull String value) {
        return value.trim().length() >= minLengthName && value.trim().length() <= maxLengthName;
    }
}