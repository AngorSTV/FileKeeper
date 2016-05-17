package filekeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileKeeper extends JFrame {

	final static String			version				= "2.0.0";
	final static Integer		build				= 5;
	private static final long	serialVersionUID	= 1L;
	private Logger				log					= LoggerFactory.getLogger(FileKeeper.class);
	private ArrayList<Task>		taskList			= new ArrayList<>();
	private Path				fileXmlTasks		= Paths.get("tasks.xml");

	private SystemTray			tray;
	private TrayIcon			trayIcon;

	public JProgressBar			progressBar;
	public JTextPane			statusBar;
	private JTable				table;

	public FileKeeper() {
		ArrayList<DirWatcher> dwList = new ArrayList<>();

		log.info("Start GUI version." + version);

		if (fileXmlTasks.toFile().exists()) {
			taskList = Lib.readXml(fileXmlTasks);
		}
		log.info("Tasks numbers: " + taskList.size());
		if (taskList.size() > 0) {
			for (Task task : taskList) {
				DirWatcher dw = new DirWatcher(task);
				dw.start();
				dwList.add(dw);
			}
		}
		initialize();
		setListeners();
		setUpTrayIcon();
	}

	private void setListeners() {

		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == ICONIFIED) {
					try {
						tray.add(trayIcon);
						setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to tray");
					}
				}
				if (e.getNewState() == 7) {
					try {
						tray.add(trayIcon);
						setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to system tray");
					}
				}
				if (e.getNewState() == MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					setVisible(true);
					System.out.println("Tray icon removed");
				}
				if (e.getNewState() == NORMAL) {
					tray.remove(trayIcon);
					setVisible(true);
					System.out.println("Tray icon removed");
				}
			}
		});

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ArrayList<Image> imageList = new ArrayList<>();

        this.setTitle("File Keeper " + FileKeeper.version);
		this.setResizable(false);
		this.setOpacity(1.0f);
		this.setBounds(100, 100, 537, 530);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Image image = new ImageIcon("images/file-keeper-icon.png").getImage();
		imageList.add(image);

		image = new ImageIcon("images/file-keeper-32-icon.png").getImage();
		imageList.add(image);

		this.setIconImages(imageList);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnConfiguration = new JMenu("Configuration");
		menuBar.add(mnConfiguration);

		Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JPanel botom = new JPanel();
		getContentPane().add(botom, BorderLayout.SOUTH);
		botom.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		botom.add(progressBar, BorderLayout.NORTH);

		statusBar = new JTextPane();
		statusBar.setEditable(false);
		botom.add(statusBar, BorderLayout.SOUTH);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		table = new JTable();
		tabbedPane.addTab("Main", (Icon) null, table, null);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Configuration", null, panel, null);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			log.error("Unable to set LookAndFeel");
		}
	}

	private void setUpTrayIcon() {

		Image image = new ImageIcon("images/file-keeper-icon.png").getImage();

		PopupMenu popup = new PopupMenu();

		MenuItem defaultItem = new MenuItem("Open");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
				setExtendedState(JFrame.NORMAL);
			}
		});
		popup.add(defaultItem);

		defaultItem = new MenuItem("Configuration");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		popup.add(defaultItem);
		popup.addSeparator();

		defaultItem = new MenuItem("Exit");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		popup.add(defaultItem);

		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon(image, "File Keeper", popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						setVisible(true);
						setExtendedState(getExtendedState() & (JFrame.ICONIFIED ^ 0xFFFF));
						requestFocus();
					}
					if (e.getClickCount() == 1 & e.getButton() == 1) {
						trayIcon.displayMessage(null, "File Keeper " + version, TrayIcon.MessageType.INFO);
					}
				}
			});
		} else {
			log.error("system tray not supported");
		}
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			// запуск консольной версии
			ConsoleMode cm = new ConsoleMode(args);
			cm.run();
			System.exit(0);
		}
		FileKeeper fileKeeper = new FileKeeper();
		fileKeeper.setVisible(true);
	}
}
