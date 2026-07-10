package github.soltaufintel.amalia.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.pmw.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class FileService {

    private FileService() {
    }
    
    public static List<String> listFolders(File folder) {
        List<String> ret = new ArrayList<>();
        if (folder.isDirectory()) {
            File[] folders = folder.listFiles();
            if (folders != null) {
                for (File f : folders) {
                    if (f.isDirectory() && !f.getName().startsWith(".")) {
                        ret.add(f.getName());
                    }
                }
            }
        }
        return ret;
    }
    
    public static String loadPlainTextFile(File file) {
    	byte[] data = loadBinaryFile(file);
    	return data == null ? null : new String(data);
    }

    public static void savePlainTextFile(File file, String content) {
        if (content == null) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(file)) {
                w.write(content);
            } catch (IOException e) {
                Logger.error(file.getAbsolutePath());
                throw new RuntimeException("Error saving file", e);
            }
        }
    }

    public static <T> T loadJsonFile(File file, Class<T> type) {
        String json = loadPlainTextFile(file);
        return json == null ? null : new Gson().fromJson(json, type);
    }

    public static <T> void saveJsonFile(File file, T data) {
        savePlainTextFile(file, data == null ? null : prettyJSON(data));
    }
    
    public static byte[] loadBinaryFile(File file) {
        if (file.isFile()) {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (Exception e) {
                Logger.error(file.getAbsolutePath());
                throw new RuntimeException("Error loading file", e);
            }
        }                
        return null;
    }

    public static void saveBinaryFile(File file, byte[] data) {
    	if (data == null) {
    		file.delete();
    	} else {
	        file.getParentFile().mkdirs();
	        try (FileOutputStream w = new FileOutputStream(file)) {
	            w.write(data);
	        } catch (IOException e) {
                Logger.error(file.getAbsolutePath());
                throw new RuntimeException("Error saving file", e);
	        }
    	}
    }
    
    public static boolean isLegalFilename(String filename) {
        return filename != null && !filename.isBlank()
                && !filename.contains("\\")
                && !filename.contains("/")
                && !filename.contains(":")
                && !filename.contains("*")
                && !filename.contains("?")
                && !filename.contains("\"")
                && !filename.contains("<")
                && !filename.contains(">")
                && !filename.contains("|");
    }
    
    public static String getSafeName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        String ret = "";
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            switch (c) {
            case '\\':
            case '/':
            case ':':
            case '*':
            case '?':
            case '"':
            case '<':
            case '>':
            case '|':
                break;
            case ' ':
                ret += '-';
                break;
            default:
                ret += c;
            }
        }
        ret = ret.replace("--", "-");
        if (ret.isEmpty()) {
            throw new RuntimeException("Safe name is empty");
        }
        return ret;
    }

    public static void deleteFolder(File folder) {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException ignore) {
        }
    }
    
    public static void copyFile(File fromFile, File toDir) {
        try {
            toDir.mkdirs();
            Files.copy(fromFile.toPath(), new File(toDir, fromFile.getName()).toPath(), //
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Logger.error(e, "Error copying file from " + fromFile.getAbsolutePath() + " to dir: " + toDir.getAbsolutePath());
            throw new RuntimeException("Error copying file. See log.");
        }
    }
    
    public static void copyFiles(File fromDir, File toDir) {
        if (fromDir != null && fromDir.isDirectory()) {
            File[] files = fromDir.listFiles();
            if (files != null && files.length > 0) {
                toDir.mkdirs();
                for (File file : files) {
                    copyFile(file, toDir);
                    try {
                        Files.copy(file.toPath(), new File(toDir, file.getName()).toPath(), //
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Logger.error(e, "Error copying file from " + file.getAbsolutePath() + " to dir: "
                                + toDir.getAbsolutePath());
                        throw new RuntimeException("Error copying files. See log.");
                    }
                }
            }
        }
    }

    public static void moveFiles(File fromDir, File toDir) {
        if (fromDir == null || !fromDir.isDirectory()) {
            return;
        }
        File[] files = fromDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                toDir.mkdirs();
                try {
                    Files.move(file.toPath(), new File(toDir, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    Logger.error(e, "Error moving file from " + file.getAbsolutePath() + " to dir: " + toDir.getAbsolutePath());
                    throw new RuntimeException("Error moving file. See log.");
                }
            } else if (file.isDirectory()) {
                moveFiles(file, new File(toDir, file.getName()));
            }
        }
    }

    public static void zip(File folder, File zipFile) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }
        zipFile.delete();
        int startOfFilenameWithRelativePath = folder.getAbsolutePath().length() + 1;
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Files.walkFileTree(folder.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    out.putNextEntry(new ZipEntry(path.toFile().getAbsolutePath()
                            .substring(startOfFilenameWithRelativePath)));
                    Files.copy(path, out);
                    out.closeEntry();
                    return super.visitFile(path, attrs);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zip given files to zipFile. Duplicate filenames are not supported.
     * @param files to add to zip file
     * @param zipFile -
     */
    public static void zip(List<File> files, File zipFile) {
        zipFile.delete();
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                out.putNextEntry(new ZipEntry(file.getName()));
                Files.copy(file.toPath(), out);
                out.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String prettyJSON(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement je = JsonParser.parseString(json);
            return gson.toJson(je);
        } catch (JsonSyntaxException e) {
            Logger.error(e);
            return json;
        }
    }

    public static <T> String prettyJSON(T data) {
        return prettyJSON(new Gson().toJson(data));
    }

    public static List<File> findFiles(File folder, String nameEndsWith) {
    	return findFiles(folder, file -> file.getName().endsWith(nameEndsWith));
    }
    
    public static List<File> findFiles(File folder, Predicate<File> test) {
    	if (!folder.isDirectory()) {
    		return List.of();
    	}
		try {
			final List<File> files = new ArrayList<>();
			Files.walkFileTree(folder.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (test.test(file.toFile())) {
						files.add(file.toFile());
					}
					return super.visitFile(file, attrs);
				}
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					String name = dir.toFile().getName();
					if ("node_modules".equals(name) || "ROLLOUT".equals(name) || "bin".equals(name) || "build".equals(name)) {
						return FileVisitResult.SKIP_SUBTREE;
					}
					return super.preVisitDirectory(dir, attrs);
				}
			});
			files.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
			return files;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * not recursive
     * @param dir -
     * @return filenames without directory, empty if dir is null or not a directory
     */
    public static List<String> loadFilenames(File dir) {
        List<String> filenames = new ArrayList<>();
        if (dir == null || !dir.isDirectory()) {
            return filenames;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                filenames.add(file.getName());
            }
        }
        return filenames;
    }

    public static boolean isDirEmpty(File dir) {
        File[] files = dir.listFiles();
        return files != null && files.length == 0;
    }

    public static long calculateFolderSize(Path folderPath) throws IOException {
        final long[] totalSize = { 0 };
        Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                totalSize[0] += attrs.size(); // Add the size of each file
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.println("Failed to access file: " + file + " (" + exc.getMessage() + ")");
                return FileVisitResult.CONTINUE; // Continue even if some files are inaccessible
            }
        });
        return totalSize[0];
    }
}
