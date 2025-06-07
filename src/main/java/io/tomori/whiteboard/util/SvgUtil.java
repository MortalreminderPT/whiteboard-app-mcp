

package io.tomori.whiteboard.util;

import io.tomori.whiteboard.exception.ParseException;
import io.tomori.whiteboard.model.shapes.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Util class for SVG import and export.
 * Provide methods to convert between whiteboard shapes and SVG format.
 */
public class SvgUtil {
    /**
     * Convert an SVG string into shapes list.
     *
     * @param svgString SVG content string
     * @return shapes list parsed from SVG
     * @throws ParseException If SVG cannot be parsed
     */
    public static CopyOnWriteArrayList<Shape> fromSvg(final String svgString) throws ParseException {
        final CopyOnWriteArrayList<Shape> shapes = new CopyOnWriteArrayList<>();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new ByteArrayInputStream(svgString.getBytes(StandardCharsets.UTF_8)));
            final NodeList circleNodes = doc.getElementsByTagName("circle");
            for (int i = 0; i < circleNodes.getLength(); i++) {
                try {
                    final Element element = (Element) circleNodes.item(i);
                    shapes.add(new CircleShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing circle: " + e.getMessage());
                }
            }
            final NodeList rectNodes = doc.getElementsByTagName("rect");
            for (int i = 0; i < rectNodes.getLength(); i++) {
                try {
                    final Element element = (Element) rectNodes.item(i);
                    shapes.add(new RectangleShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing rectangle: " + e.getMessage());
                }
            }
            final NodeList lineNodes = doc.getElementsByTagName("line");
            for (int i = 0; i < lineNodes.getLength(); i++) {
                try {
                    final Element element = (Element) lineNodes.item(i);
                    shapes.add(new LineShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing line: " + e.getMessage());
                }
            }
            final NodeList pathNodes = doc.getElementsByTagName("path");
            for (int i = 0; i < pathNodes.getLength(); i++) {
                try {
                    final Element element = (Element) pathNodes.item(i);
                    shapes.add(new PathShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing path: " + e.getMessage());
                }
            }
            final NodeList polygonNodes = doc.getElementsByTagName("polygon");
            for (int i = 0; i < polygonNodes.getLength(); i++) {
                try {
                    final Element element = (Element) polygonNodes.item(i);
                    shapes.add(new TriangleShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing polygon: " + e.getMessage());
                }
            }
            final NodeList ellipseNodes = doc.getElementsByTagName("ellipse");
            for (int i = 0; i < ellipseNodes.getLength(); i++) {
                try {
                    final Element element = (Element) ellipseNodes.item(i);
                    shapes.add(new OvalShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing ellipse: " + e.getMessage());
                }
            }
            final NodeList textNodes = doc.getElementsByTagName("text");
            for (int i = 0; i < textNodes.getLength(); i++) {
                try {
                    final Element element = (Element) textNodes.item(i);
                    shapes.add(new TextShape(element));
                } catch (final Exception e) {
                    System.out.println("Error parsing text: " + e.getMessage());
                }
            }
        } catch (final Exception e) {
            System.out.println("SVG parsing error: " + e.getMessage());
            throw new ParseException("Failed to import SVG: " + e.getMessage());
        }
        return shapes;
    }

    /**
     * Convert shapes list into SVG string.
     *
     * @param shapes shapes list to convert
     * @return SVG content string
     */
    public static String toSvg(final List<Shape> shapes) {
        String svgString = "";
        svgString += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
        svgString += "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n";
        for (final Shape shape : shapes) {
            svgString += "  " + shape.toSvgString() + "\n";
        }
        svgString += "</svg>";
        return svgString;
    }
}
