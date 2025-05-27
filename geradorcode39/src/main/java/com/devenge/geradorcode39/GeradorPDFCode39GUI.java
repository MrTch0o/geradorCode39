package com.devenge.geradorcode39;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class GeradorPDFCode39GUI extends JFrame {

    private JTextField startField;
    private JTextField endField;
    private JButton gerarButton;

    public GeradorPDFCode39GUI() {
        setTitle("Gerador Code39 PDF");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Início (0-9999):"));
        startField = new JTextField();
        add(startField);

        add(new JLabel("Fim (0-9999):"));
        endField = new JTextField();
        add(endField);

        gerarButton = new JButton("Gerar PDF");
        gerarButton.addActionListener(e -> gerarPDF());
        add(gerarButton);

        setVisible(true);
    }

    private void gerarPDF() {
        try {
            int start = Integer.parseInt(startField.getText());
            int end = Integer.parseInt(endField.getText());

            if (start < 0 || end > 9999 || start > end) {
                JOptionPane.showMessageDialog(this, "Os números devem estar entre 0 e 9999, e início ≤ fim.");
                return;
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("codigos_barras.pdf"));
            document.open();

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            for (int i = start; i <= end; i++) {
                String codigo = String.format("%04d", i);
                com.lowagie.text.Image img = gerarImagemCodigoBarras(codigo);

                PdfPCell cell = new PdfPCell(img, true);
                cell.setPadding(10);
                cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                table.addCell(cell);
            }

            int remainder = (end - start + 1) % 3;
            if (remainder != 0) {
                int emptyCells = 3 - remainder;
                for (int i = 0; i < emptyCells; i++) {
                    PdfPCell emptyCell = new PdfPCell();
                    emptyCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                    table.addCell(emptyCell);
                }
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF gerado com sucesso!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira números válidos.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + e.getMessage());
        }
    }

    private com.lowagie.text.Image gerarImagemCodigoBarras(String codigo) throws Exception {
        Code39Bean bean = new Code39Bean();
        final int dpi = 150;

        bean.setModuleWidth(0.2);
        bean.setWideFactor(3);
        bean.doQuietZone(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi,
                BufferedImage.TYPE_BYTE_BINARY, false, 0);

        bean.generateBarcode(canvas, codigo);
        canvas.finish();

        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
        return com.lowagie.text.Image.getInstance(bufferedImage, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GeradorPDFCode39GUI::new);
    }
}
