package fm.bootifulpodcast.desktop.client;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
public class PodcastArchiveBuilder {

	private final Map<String, Optional<Media>> media = new ConcurrentHashMap<>();

	private String description, uid, title;

	private String MP3_EXT = "mp3";

	private String WAV_EXT = "wav";

	private File archivePackage;

	public PodcastArchiveBuilder(String title, String description, String uuid) {
		this.description = description;
		this.uid = uuid;
		this.title = title;
		this.media.put(MP3_EXT, Optional.empty());
		this.media.put(WAV_EXT, Optional.empty());
	}

	@SneakyThrows
	private static File doCreatePackage(String title, String description, String uid,
			Media mp3, Media wav, File destination) {

		var staging = Files.createTempDirectory("staging").toFile();

		var xmlFile = new File(staging, "manifest.xml");
		try (var xmlOutputStream = new BufferedWriter(new FileWriter(xmlFile))) {
			var xml = buildXmlManifestForPackage(title, description, uid, mp3, wav);
			FileCopyUtils.copy(xml, xmlOutputStream);
			log.debug("wrote " + xmlFile.getAbsolutePath() + " with content " + xml);
		}

		var zipFile = destination == null
				? new File(staging, UUID.randomUUID().toString() + ".zip") : destination;
		Assert.isTrue(zipFile.getName().endsWith(".zip"),
				"the output file name for the archive must end in .zip");
		var srcFiles = new ArrayList<File>();
		srcFiles.add(xmlFile);
		addMediaFilesToPackage(mp3, srcFiles);
		addMediaFilesToPackage(wav, srcFiles);

		try (var outputStream = new BufferedOutputStream(new FileOutputStream(zipFile));
				var zipOutputStream = new ZipOutputStream(outputStream)) {
			for (var fileToZip : srcFiles) {
				try (var inputStream = new BufferedInputStream(
						new FileInputStream(fileToZip))) {
					var zipEntry = new ZipEntry(fileToZip.getName());
					zipOutputStream.putNextEntry(zipEntry);
					StreamUtils.copy(inputStream, zipOutputStream);
				}
			}
		}
		return zipFile;
	}

	private static void addElementFor(Document doc, Element root, String elementName,
			Map<String, String> attrs) {
		Element element = doc.createElement(elementName);
		attrs.forEach(element::setAttribute);
		root.appendChild(element);
	}

	private static void addAttributesForMedia(Document doc, Element root, Media media) {
		if (null == media) {
			return;
		}
		var intro = media.getIntro();
		var interview = media.getInterview();
		var attrs = Map.of("intro", intro.getName(), "interview", interview.getName());
		addElementFor(doc, root, media.getFormat(), attrs);
	}

	@SneakyThrows
	private static String buildXmlManifestForPackage(String title, String description,
			String uid, Media mp3, Media wav) {

		var docFactory = DocumentBuilderFactory.newInstance();
		var docBuilder = docFactory.newDocumentBuilder();

		var doc = docBuilder.newDocument();
		var rootElement = doc.createElement("podcast");
		rootElement.setAttribute("description", description);
		rootElement.setAttribute("title", title);
		rootElement.setAttribute("uid", uid);
		doc.appendChild(rootElement);

		addAttributesForMedia(doc, rootElement, mp3);
		addAttributesForMedia(doc, rootElement, wav);

		var transformerFactory = TransformerFactory.newInstance();

		var transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		var source = new DOMSource(doc);
		var stringWriter = new StringWriter();
		var result = new StreamResult(stringWriter);
		transformer.transform(source, result);
		return stringWriter.toString();
	}

	private static void addMediaFilesToPackage(Media m, Collection<File> files) {
		if (null == m)
			return;
		files.add(m.getInterview());
		files.add(m.getIntro());
	}

	private PodcastArchiveBuilder addMedia(String ext, File intro, File interv) {
		return this.addMedia(ext, new Media(ext, intro, interv));
	}

	public PodcastArchiveBuilder addMp3Media(File intro, File interview) {
		return this.addMedia(MP3_EXT, intro, interview);
	}

	public PodcastArchiveBuilder addWavMedia(File intro, File interview) {
		return this.addMedia(WAV_EXT, intro, interview);
	}

	private PodcastArchiveBuilder addMedia(String ext, Media media) {
		this.media.put(ext, Optional.of(media));
		return this;
	}

	public File build() {
		this.archivePackage = doCreatePackage(this.title, this.description, this.uid,
				this.media.get(MP3_EXT).orElse(null),
				this.media.get(WAV_EXT).orElse(null), null);
		return this.archivePackage;
	}

	public File build(File archiveDestination) {
		this.archivePackage = doCreatePackage(this.title, this.description, this.uid,
				this.media.get(MP3_EXT).orElse(null),
				this.media.get(WAV_EXT).orElse(null), archiveDestination);
		return this.archivePackage;
	}

}
