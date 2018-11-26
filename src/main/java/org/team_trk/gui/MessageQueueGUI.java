package org.team_trk.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MessageQueueGUI extends Application {

	private static MessageQueueGUI instance;

	public static MessageQueueGUI getInstance() {
		return instance;
	}

	private Map<String, AgentInfo> agentinfos;

	private ListView<BorderPane> layout;

	public MessageQueueGUI() {
		super();
		if (instance != null) {
			throw new RuntimeException("GUI already initialized! Use getInstance instead.");
		}
		MessageQueueGUI.instance = this;
		agentinfos = new HashMap<>();
	}

	public static MessageQueueGUI open(String[] args) {
		try {
			launch(MessageQueueGUI.class, args);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return instance;
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle(MessageQueueGUI.class.getSimpleName());

		layout = new ListView<>();
		BorderPane borderPane = new BorderPane();
		Label n = new Label("Name");
		Label a = new Label("Amount");
		n.setTextAlignment(TextAlignment.CENTER);
		a.setTextAlignment(TextAlignment.CENTER);
		n.setFont(Font.font(n.getFont().getSize() * 1.5));
		a.setFont(Font.font(a.getFont().getSize() * 1.5));
		borderPane.setLeft(n);
		borderPane.setRight(a);
		layout.getItems().add(borderPane);
		primaryStage.setScene(new Scene(layout, 500, 300));
		primaryStage.show();
	}

	@Override
	public void stop() {
		Platform.exit();
	}

	public void update(Map<String, Integer> map) {
		System.out.println(map);
		HashSet<String> oldSet = new HashSet<String>(agentinfos.keySet());
		for (String key : map.keySet()) {
			if (agentinfos.get(key) != null) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						agentinfos.get(key).setAmount(map.get(key));
					}
				});
				oldSet.removeIf(Predicate.isEqual(key));
			} else {
				AgentInfo info = new AgentInfo(key, map.get(key));
				agentinfos.put(key, info);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						layout.getItems().add(info);
					}
				});
			}
		}
		for (String remaining : oldSet) {
			agentinfos.put(remaining, null);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					layout.getItems().remove(agentinfos.get(remaining));
				}
			});
		}
	}

	class AgentInfo extends BorderPane {

		private Label name;

		private Label amount;

		public AgentInfo(String name, Integer amount) {
			amount = amount != null ? amount : 0;
			this.name = new Label(name);
			this.amount = new Label("" + amount);
			setLeft(this.name);
			setRight(this.amount);
			this.name.setTextAlignment(TextAlignment.CENTER);
			this.amount.setTextAlignment(TextAlignment.CENTER);
		}

		public String getName() {
			return name.getText();
		}

		public void setName(String name) {
			this.name.setText(name);
		}

		public Integer getAmount() {
			return Integer.parseInt(amount.getText());
		}

		public void setAmount(Integer amount) {
			amount = amount != null ? amount : 0;
			this.amount.setText("" + amount);
		}

	}
}