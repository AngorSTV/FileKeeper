package filekeeper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Lib {

	private static Logger	log	= LoggerFactory.getLogger(Lib.class);

	public static ArrayList<Task> readXml(Path file) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(file.toFile());

			NodeList tasks = document.getElementsByTagName("task");
			for (int i = 0; i < tasks.getLength(); i++) {
				Task task = new Task();
				Node node = tasks.item(i);
				NamedNodeMap map = node.getAttributes();
				task.setName(map.getNamedItem("name").getTextContent());
				task.setSource(Paths.get(map.getNamedItem("source").getTextContent()));
				task.setDest(Paths.get(map.getNamedItem("dest").getTextContent()));
				if (map.getNamedItem("mode").getTextContent().equals("true")) {
					task.setFastMode(true);
				} else {
					task.setFastMode(false);
				}
				taskList.add(task);
			}
		} catch (ParserConfigurationException e) {
			log.error(e.getLocalizedMessage());
		} catch (SAXException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}

		return taskList;

	}

	public static boolean isSameFile(Path source, Path dest, boolean fastMode) {

		// проверка на наличие файла назначения
		if (Files.notExists(dest)) {
			return false;
		}

		// проверка на размер
		if (source.toFile().length() != dest.toFile().length()) {
			return false;
		}

		// TODO проверка на атрибуты очень сомнительная надёжность но высокая
		// скорость
		if (fastMode) {
			try {
				if (Files.getLastModifiedTime(source).equals(Files.getLastModifiedTime(dest))) {
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// сравнение хешей всего файла
		// добавить более интелектуальный алгоритм хэша
		String sourceSHA = doHash(source);
		String destSHA = doHash(dest);
		if (sourceSHA.equals(destSHA)) {
			return true;
		}
		return false;
	}

	private static String doHash(Path fileName) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try (InputStream is = Files.newInputStream(fileName)) {
			DigestInputStream dis = new DigestInputStream(is, md);

			byte[] buffer = new byte[4096];
			while ((dis.read(buffer)) != -1) {
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] digest = md.digest();
		return new BigInteger(1, digest).toString(16);
	}
}
