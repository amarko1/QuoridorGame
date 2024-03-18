package hr.algebra.quoridorgamejava2.utils;

import hr.algebra.quoridorgamejava2.model.GameMoveSorter;
import hr.algebra.quoridorgamejava2.model.GameMove;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class XmlUtils {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss:SS");
    public static final String FILENAME = "xml/gameMoves.xml";

    public static void saveGameMove(GameMove gameMove) {
        Set<GameMove> gameMoveList = new HashSet<>();

        if (Files.exists(Path.of(FILENAME))){
            gameMoveList.addAll(XmlUtils.readAllGameMoves());
        }

        gameMoveList.add(gameMove);

        try {
            Document document = createDocument("gameMoves");
            for (GameMove gm : gameMoveList) {
                Element gameMoves = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMoves);

                gameMoves.appendChild(createElement(document, "player", gm.getPlayer()));
                gameMoves.appendChild(createElement(document, "position", gm.getPosition()));
                gameMoves.appendChild(createElement(document, "localDateTime",
                        gm.getLocalDateTime().format(dateTimeFormatter)));
            }
            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, element, null);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(FILENAME)));
    }

    public static Set<GameMove> readAllGameMoves() {
        SortedSet<GameMove> gameMoves = new TreeSet<>(new GameMoveSorter());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(FILENAME));

            Element documentElement = document.getDocumentElement();
            System.out.println("Procitao sam: " + documentElement.getTagName());

            NodeList gameMovesChildList = documentElement.getChildNodes();

            for (int i = 0; i < gameMovesChildList.getLength(); i++){

                Node gameMoveNode = gameMovesChildList.item(i);

                if (gameMoveNode.getNodeType() == Node.ELEMENT_NODE){


                    String player = "";
                    String position = "";
                    LocalDateTime localDateTime = LocalDateTime.now();

                    Element gameMoveElement = (Element) gameMoveNode;
                    System.out.println("Procitao sam: " + gameMoveElement.getTagName());

                    NodeList gameMoveChildList = gameMoveElement.getChildNodes();

                    for (int j = 0; j < gameMoveChildList.getLength(); j++){

                        Node gameMoveChildNode = gameMoveChildList.item(j);

                        if (gameMoveChildNode.getNodeType() == Node.ELEMENT_NODE){

                            Element gameMoveChildElement = (Element) gameMoveChildNode;

                            switch (gameMoveChildElement.getTagName()){
                                case "player" -> player = gameMoveChildElement.getTextContent();
                                case "position" -> position = gameMoveChildElement.getTextContent();
                                case "localDateTime" -> localDateTime = LocalDateTime.parse(
                                        gameMoveChildElement.getTextContent(), dateTimeFormatter);

                            }
                        }
                    }
                    GameMove newGame = new GameMove(player, position, localDateTime);
                    gameMoves.add(newGame);
                }
            }
        }
        catch (ParserConfigurationException | SAXException | IOException ex){
            ex.printStackTrace();
        }

        return gameMoves;
    }
}
