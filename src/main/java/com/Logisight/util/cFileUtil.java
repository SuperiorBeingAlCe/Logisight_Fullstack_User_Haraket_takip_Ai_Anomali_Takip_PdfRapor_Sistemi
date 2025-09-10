package com.Logisight.util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class cFileUtil {

    private static final String REPORTS_DIR = "reports";

    /**
     * Rapor dizinini oluştur (yoksa)
     */
    public static Path ensureReportsDirectory() throws IOException {
        Path reportPath = Paths.get(REPORTS_DIR);
        if (!Files.exists(reportPath)) {
            Files.createDirectories(reportPath);
        }
        return reportPath;
    }

    /**
     * Belirli bir dosya adı için tam dosya yolunu verir
     */
    public static Path getReportFilePath(String fileName) throws IOException {
        ensureReportsDirectory();
        return Paths.get(REPORTS_DIR, fileName);
    }

    /**
     * PDF dosyasını sistemde kaydet
     */
    public static File savePdfFile(byte[] pdfBytes, String fileName) throws IOException {
        Path filePath = getReportFilePath(fileName);
        Files.write(filePath, pdfBytes);
        return filePath.toFile();
    }

    /**
     * Dosya varsa sil
     */
    public static boolean deleteFileIfExists(String fileName) throws IOException {
        Path path = getReportFilePath(fileName);
        return Files.deleteIfExists(path);
    }

    /**
     * Rapor dosyasının var olup olmadığını kontrol et
     */
    public static boolean doesReportExist(String fileName) throws IOException {
        return Files.exists(getReportFilePath(fileName));
    }
}
