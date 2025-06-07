package app;

import core.objects.LabWork;
import core.utils.UTF8Control;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainController {
    @FXML private MenuItem currentUserMenuItem;
    @FXML private TextField filterField;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageLabel;

    @FXML private TableView<LabWork> tableView;
    @FXML private TableColumn<LabWork, String> colId;
    @FXML private TableColumn<LabWork, String> colName;
    @FXML private TableColumn<LabWork, String> colX;
    @FXML private TableColumn<LabWork, String> colY;
    @FXML private TableColumn<LabWork, String> colCreationDate;
    @FXML private TableColumn<LabWork, String> colMinimalPoint;
    @FXML private TableColumn<LabWork, String> colDescription;
    @FXML private TableColumn<LabWork, String> colDifficulty;
    @FXML private TableColumn<LabWork, String> colAuthorName;
    @FXML private TableColumn<LabWork, String> colAuthorWeight;
    @FXML private TableColumn<LabWork, String> colAuthorEyeColor;
    @FXML private TableColumn<LabWork, String> colAuthorHairColor;
    @FXML private TableColumn<LabWork, String> colNationality;
    @FXML private TableColumn<LabWork, String> colOwner;

    @FXML private Canvas scatterCanvas;

    @FXML private Canvas detailCanvas;

    @FXML private VBox bottomVBox;

    @FXML private Label statusLabel;

    private ContextMenu tableContextMenu;
    private MenuItem editContextItem;
    private MenuItem removeContextItem;

    private final ObservableList<LabWork> masterDataFull = FXCollections.observableArrayList();
    private final ObservableList<LabWork> filteredData   = FXCollections.observableArrayList();
    private static final int PAGE_SIZE = 20;
    private int currentPage = 1;
    private int totalPages  = 1;

    private ServerApi serverApi;
    private ResourceBundle bundle;
    private String currentLogin;

    private LabWork selectedLabWork;

    private Timeline pollingTimeline;
    private Timeline scatterPulseTimeline;
    private Timeline detailPulseTimeline;

    private double scatterAlpha = 0.5;
    private boolean scatterGrowing = true;
    private double detailPhase = 0.0;

    @FXML
    private void onLangEn() {
        MainApp.setLocale(Locale.CANADA);
        MainApp.showMain(currentLogin, serverApi.getToken(), serverApi.getRoles());
    }

    @FXML
    private void onLangRu() {
        MainApp.setLocale(new Locale("ru"));
        MainApp.showMain(currentLogin, serverApi.getToken(), serverApi.getRoles());
    }

    @FXML
    private void onLangMk() {
        MainApp.setLocale(new Locale("mk"));
        MainApp.showMain(currentLogin, serverApi.getToken(), serverApi.getRoles());
    }

    @FXML
    private void onLangPl() {
        MainApp.setLocale(new Locale("pl"));
        MainApp.showMain(currentLogin, serverApi.getToken(), serverApi.getRoles());
    }

    @FXML
    private void initialize() {
        setupTableColumns();

        tableView.setRowFactory(tv -> {
            TableRow<LabWork> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    LabWork lw = row.getItem();
                    showEditDialog(lw);
                }
            });
            return row;
        });

        editContextItem   = new MenuItem("EDIT");
        removeContextItem = new MenuItem("REMOVE");
        editContextItem.setOnAction(e -> onUpdate());
        removeContextItem.setOnAction(e -> onRemove());
        tableContextMenu = new ContextMenu(editContextItem, removeContextItem);
        tableView.setContextMenu(tableContextMenu);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw == null) {
                return;
            }
            System.out.println("■ Выбрали элемент: ID=" + nw.getId());
            selectedLabWork = nw;
            drawScatter();
            showDetailFor(nw);
        });

        prevPageButton.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                applyFilterAndPagination();
                drawScatter();
                updatePageLabel();
            }
        });
        nextPageButton.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                applyFilterAndPagination();
                drawScatter();
                updatePageLabel();
            }
        });

        filterField.setOnKeyReleased(this::onFilterChanged);

        scatterCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onScatterClicked);

        tableView.setItems(filteredData);

        detailCanvas.widthProperty().bind(bottomVBox.widthProperty());
    }

    public void setCurrentUser(String login, String jwtToken, Set<String> roles) {
        this.currentLogin = login;

        this.bundle = ResourceBundle.getBundle(
                "bundles.messages",
                MainApp.getLocale(),
                new UTF8Control()
        );

        currentUserMenuItem.setText(login);

        editContextItem.textProperty().bind(bundleProperty("btn.update"));
        removeContextItem.textProperty().bind(bundleProperty("btn.remove"));

        this.serverApi = new ServerApi();
        serverApi.setToken(jwtToken);
        serverApi.setRoles(roles);
        setupTableColumns();
        loadDataFromServer();
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
        pollingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), ev -> loadDataFromServerNoStatus())
        );
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();

        if (scatterPulseTimeline != null) {
            scatterPulseTimeline.stop();
        }
        scatterPulseTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), ev -> {
                    if (scatterGrowing) {
                        scatterAlpha += 0.02;
                        if (scatterAlpha >= 1.0) {
                            scatterAlpha = 1.0;
                            scatterGrowing = false;
                        }
                    } else {
                        scatterAlpha -= 0.02;
                        if (scatterAlpha <= 0.3) {
                            scatterAlpha = 0.3;
                            scatterGrowing = true;
                        }
                    }
                    drawScatter();
                })
        );
        scatterPulseTimeline.setCycleCount(Timeline.INDEFINITE);
        scatterPulseTimeline.play();

        stopDetailAnimation();
        clearDetailCanvas();
    }

    private void setupTableColumns() {
        Locale loc = MainApp.getLocale();
        NumberFormat intFmt = NumberFormat.getIntegerInstance(loc);
        NumberFormat dblFmt = NumberFormat.getNumberInstance(loc);

        // ID
        colId.setText(bundle == null ? "ID" : bundle.getString("col.id"));
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(intFmt.format(c.getValue().getId()))
        );
        colId.setSortable(true);

        // Name
        colName.setText(bundle == null ? "Name" : bundle.getString("col.name"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setSortable(true);

        // X
        colX.setText(bundle == null ? "X" : bundle.getString("col.x"));
        colX.setCellValueFactory(c ->
                new SimpleStringProperty(dblFmt.format(c.getValue().getCoordinates().getX()))
        );
        colX.setSortable(true);

        // Y
        colY.setText(bundle == null ? "Y" : bundle.getString("col.y"));
        colY.setCellValueFactory(c ->
                new SimpleStringProperty(intFmt.format(c.getValue().getCoordinates().getY()))
        );
        colY.setSortable(true);

        // CreationDate
        colCreationDate.setText(bundle == null ? "Created" : bundle.getString("col.creationDate"));
        DateTimeFormatter dateFmt = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(loc);
        colCreationDate.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getCreationDate().format(dateFmt)
                )
        );
        colCreationDate.setSortable(true);

        // MinimalPoint
        colMinimalPoint.setText(bundle == null ? "Min Point" : bundle.getString("col.minimalPoint"));
        colMinimalPoint.setCellValueFactory(c ->
                new SimpleStringProperty(dblFmt.format(c.getValue().getMinimalPoint()))
        );
        colMinimalPoint.setSortable(true);

        // Description
        colDescription.setText(bundle == null ? "Description" : bundle.getString("col.description"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDescription.setSortable(true);

        // Difficulty
        colDifficulty.setText(bundle == null ? "Difficulty" : bundle.getString("col.difficulty"));
        colDifficulty.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDifficulty() == null
                                ? ""
                                : c.getValue().getDifficulty().name()
                )
        );
        colDifficulty.setSortable(true);

        // AuthorName
        colAuthorName.setText(bundle == null ? "Author" : bundle.getString("col.authorName"));
        colAuthorName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAuthor().getName())
        );
        colAuthorName.setSortable(true);

        // AuthorWeight
        colAuthorWeight.setText(bundle == null ? "Weight" : bundle.getString("col.authorWeight"));
        colAuthorWeight.setCellValueFactory(c ->
                new SimpleStringProperty(intFmt.format(c.getValue().getAuthor().getWeight()))
        );
        colAuthorWeight.setSortable(true);

        // AuthorEyeColor
        colAuthorEyeColor.setText(bundle == null ? "Eye Color" : bundle.getString("col.authorEyeColor"));
        colAuthorEyeColor.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getAuthor().getEyeColor() == null
                                ? ""
                                : c.getValue().getAuthor().getEyeColor().name()
                )
        );
        colAuthorEyeColor.setSortable(true);

        // AuthorHairColor
        colAuthorHairColor.setText(bundle == null ? "Hair Color" : bundle.getString("col.authorHairColor"));
        colAuthorHairColor.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAuthor().getHairColor().name())
        );
        colAuthorHairColor.setSortable(true);

        // Nationality
        colNationality.setText(bundle == null ? "Nationality" : bundle.getString("col.nationality"));
        colNationality.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAuthor().getNationality().name())
        );
        colNationality.setSortable(true);

        // OwnerLogin
        colOwner.setText(bundle == null ? "Owner" : bundle.getString("col.owner"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerLogin"));
        colOwner.setSortable(true);
    }

    @FXML
    private void onFilterChanged(KeyEvent event) {
        currentPage = 1;
        applyFilterAndPagination();
        drawScatter();
        updatePageLabel();
    }

    private void loadDataFromServer() {
        Integer previouslySelectedId = (selectedLabWork == null ? null : selectedLabWork.getId());

        List<LabWork> all = serverApi.fetchAll();
        masterDataFull.setAll(all);

        totalPages = (int) Math.ceil((double) masterDataFull.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        applyFilterAndPagination();
        preserveSelection(previouslySelectedId);

        drawScatter();
        updatePageLabel();
    }

    private void loadDataFromServerNoStatus() {
        Integer previouslySelectedId = (selectedLabWork == null ? null : selectedLabWork.getId());

        List<LabWork> all = serverApi.fetchAll();
        masterDataFull.setAll(all);

        totalPages = (int) Math.ceil((double) masterDataFull.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        applyFilterAndPagination();
        preserveSelection(previouslySelectedId);

        drawScatter();
        updatePageLabel();
    }

    private void preserveSelection(Integer idToRestore) {
        if (idToRestore == null) {
            selectedLabWork = null;
            tableView.getSelectionModel().clearSelection();
            showDetailFor(null);
            return;
        }

        LabWork foundInMaster = masterDataFull.stream()
                .filter(lw -> lw.getId() == idToRestore)
                .findFirst()
                .orElse(null);

        if (foundInMaster == null) {
            selectedLabWork = null;
            tableView.getSelectionModel().clearSelection();
            showDetailFor(null);
            return;
        }

        String filterText = filterField.getText().trim().toLowerCase();
        List<LabWork> filteredAll;
        if (filterText.isEmpty()) {
            filteredAll = masterDataFull;
        } else {
            filteredAll = masterDataFull.stream()
                    .filter(lw -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(lw.getName()).append(" ")
                                .append(lw.getCoordinates().getX()).append(" ")
                                .append(lw.getCoordinates().getY()).append(" ")
                                .append(lw.getMinimalPoint()).append(" ")
                                .append(lw.getDescription()).append(" ");
                        if (lw.getDifficulty() != null) {
                            sb.append(lw.getDifficulty().name()).append(" ");
                        }
                        sb.append(lw.getAuthor().getName()).append(" ")
                                .append(lw.getAuthor().getWeight()).append(" ");
                        if (lw.getAuthor().getEyeColor() != null) {
                            sb.append(lw.getAuthor().getEyeColor().name()).append(" ");
                        }
                        sb.append(lw.getAuthor().getHairColor().name()).append(" ")
                                .append(lw.getAuthor().getNationality().name()).append(" ")
                                .append(lw.getOwnerLogin());
                        return sb.toString().toLowerCase().contains(filterText);
                    })
                    .collect(Collectors.toList());
        }

        int indexInFiltered = -1;
        for (int i = 0; i < filteredAll.size(); i++) {
            if (filteredAll.get(i).getId() == idToRestore) {
                indexInFiltered = i;
                break;
            }
        }

        if (indexInFiltered < 0) {
            selectedLabWork = null;
            tableView.getSelectionModel().clearSelection();
            showDetailFor(null);
            return;
        }

        int pageContaining = indexInFiltered / PAGE_SIZE + 1;
        currentPage = pageContaining;
        applyFilterAndPagination();

        LabWork itemOnThisPage = filteredData.stream()
                .filter(lw -> lw.getId() == idToRestore)
                .findFirst()
                .orElse(null);
        if (itemOnThisPage != null) {
            tableView.getSelectionModel().select(itemOnThisPage);
            selectedLabWork = itemOnThisPage;
        }

        showDetailFor(selectedLabWork);
    }

    private void applyFilterAndPagination() {
        String filterText = filterField.getText().trim().toLowerCase();

        List<LabWork> filteredAll;
        if (filterText.isEmpty()) {
            filteredAll = masterDataFull;
        } else {
            filteredAll = masterDataFull.stream()
                    .filter(lw -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(lw.getName()).append(" ")
                                .append(lw.getCoordinates().getX()).append(" ")
                                .append(lw.getCoordinates().getY()).append(" ")
                                .append(lw.getMinimalPoint()).append(" ")
                                .append(lw.getDescription()).append(" ");
                        if (lw.getDifficulty() != null) {
                            sb.append(lw.getDifficulty().name()).append(" ");
                        }
                        sb.append(lw.getAuthor().getName()).append(" ")
                                .append(lw.getAuthor().getWeight()).append(" ");
                        if (lw.getAuthor().getEyeColor() != null) {
                            sb.append(lw.getAuthor().getEyeColor().name()).append(" ");
                        }
                        sb.append(lw.getAuthor().getHairColor().name()).append(" ")
                                .append(lw.getAuthor().getNationality().name()).append(" ")
                                .append(lw.getOwnerLogin());
                        return sb.toString().toLowerCase().contains(filterText);
                    })
                    .collect(Collectors.toList());
        }

        int newSize = filteredAll.size();
        totalPages = (int) Math.ceil((double) newSize / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex   = Math.min(fromIndex + PAGE_SIZE, newSize);
        List<LabWork> pageList = filteredAll.subList(fromIndex, toIndex);

        filteredData.setAll(pageList);
    }

    private void updatePageLabel() {
        pageLabel.setText(String.format("%d / %d", currentPage, totalPages));
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);
    }

    private void drawScatter() {
        GraphicsContext gc = scatterCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, scatterCanvas.getWidth(), scatterCanvas.getHeight());

        double baseSize = 20.0;
        double half = baseSize / 2.0;

        for (LabWork lw : filteredData) {
            double x = lw.getCoordinates().getX();
            double y = lw.getCoordinates().getY();

            boolean isOwner = lw.getOwnerLogin().equals(currentLogin);
            Color fillColor = isOwner ? Color.BLUE : Color.GRAY;
            gc.setFill(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), scatterAlpha));
            gc.fillOval(x - half, y - half, baseSize, baseSize);

            if (lw.equals(selectedLabWork)) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(2.0 + Math.abs(Math.sin(detailPhase)) * 2.0);
                gc.strokeOval(x - half - 2, y - half - 2, baseSize + 4, baseSize + 4);
            }
        }
    }

    private void onScatterClicked(MouseEvent event) {
        double clickX = event.getX();
        double clickY = event.getY();
        double half = 20.0 / 2.0;

        for (LabWork lw : filteredData) {
            double centerX = lw.getCoordinates().getX();
            double centerY = lw.getCoordinates().getY();
            double dx = clickX - centerX;
            double dy = clickY - centerY;
            if (dx * dx + dy * dy <= half * half) {
                tableView.getSelectionModel().select(lw);
                return;
            }
        }
    }

    private void showDetailFor(LabWork lw) {
        stopDetailAnimation();

        if (lw == null) {
            clearDetailCanvas();
            return;
        }

        selectedLabWork = lw;

        detailPhase = 0.0;

        detailPulseTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), ev -> {
                    detailPhase += 0.05;
                    if (detailPhase > Math.PI * 2) {
                        detailPhase -= Math.PI * 2;
                    }
                    drawDetail(lw);
                })
        );
        detailPulseTimeline.setCycleCount(Timeline.INDEFINITE);
        detailPulseTimeline.play();
    }

    private void stopDetailAnimation() {
        if (detailPulseTimeline != null) {
            detailPulseTimeline.stop();
            detailPulseTimeline = null;
        }
    }

    private void clearDetailCanvas() {
        GraphicsContext gc = detailCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, detailCanvas.getWidth(), detailCanvas.getHeight());
    }

    private void drawDetail(LabWork lw) {
        GraphicsContext gc = detailCanvas.getGraphicsContext2D();
        double canvasW = detailCanvas.getWidth();
        double canvasH = detailCanvas.getHeight();

        gc.clearRect(0, 0, canvasW, canvasH);
        if (lw == null) {
            return;
        }

        double tubeWidth  = 100;
        double tubeHeight = 160;
        double tubeX = (canvasW - tubeWidth) / 2.0;
        double tubeY = (canvasH - tubeHeight) / 2.0;
        double arcRadius = tubeWidth * 0.5;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        gc.strokeOval(tubeX, tubeY, tubeWidth, arcRadius);

        double topMidY = tubeY + arcRadius / 2.0;
        double bottomY = tubeY + tubeHeight;
        gc.strokeLine(tubeX,             topMidY, tubeX,           bottomY);
        gc.strokeLine(tubeX + tubeWidth, topMidY, tubeX + tubeWidth, bottomY);

        gc.strokeLine(tubeX, bottomY, tubeX + tubeWidth, bottomY);

        double innerLeftX   = tubeX + 2;
        double innerRightX  = tubeX + tubeWidth - 2;
        double innerWidth   = innerRightX - innerLeftX;
        double innerTopY    = tubeY + arcRadius / 2.0 + 2;
        double innerBottomY = bottomY - 2;
        double innerHeight  = innerBottomY - innerTopY;

        double baseRatio  = 0.5;   // 50% высоты пробирки
        double amplitude  = 0.2;   // ±20%
        double sineValue  = Math.sin(detailPhase);
        double levelRatio = baseRatio + amplitude * sineValue;
        if (levelRatio < 0) levelRatio = 0;
        if (levelRatio > 1) levelRatio = 1;

        double fluidHeight = innerHeight * levelRatio;
        double fluidTopY   = innerBottomY - fluidHeight;

        int hash = (int)(lw.getId() * 31 + lw.getMinimalPoint() * 17);
        hash = Math.abs(hash);
        float hue = (hash % 360) / 360.0f;
        Color fluidBase = Color.hsb(hue * 360, 0.7, 0.7, 0.6);

        gc.setFill(fluidBase);
        gc.fillRect(innerLeftX, fluidTopY, innerWidth, fluidHeight);

        gc.setFill(Color.WHITE.deriveColor(1, 1, 1, 0.15));
        double highlightX = innerLeftX + innerWidth * 0.05;
        double highlightY = fluidTopY + 4;
        double highlightW = innerWidth * 0.15;
        double highlightH = fluidHeight - 8;
        if (highlightH > 0) {
            gc.fillRoundRect(highlightX, highlightY, highlightW, highlightH,
                    arcRadius / 4.0, arcRadius / 4.0);
        }

        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setTextAlign(TextAlignment.CENTER);
        String idText   = "ID: "   + lw.getId();
        String minPText = "Min: "  + lw.getMinimalPoint();
        String diffText = "Diff: " + (lw.getDifficulty() == null ? "—" : lw.getDifficulty().name());
        double textX = canvasW / 2.0;
        double textY = fluidTopY + fluidHeight * 0.5;
        gc.fillText(idText,   textX, textY - 10);
        gc.fillText(minPText, textX, textY +  6);
        gc.fillText(diffText, textX, textY + 22);

        double personWidth  = 40;
        double personHeight = 80;
        double personX = tubeX + tubeWidth + 20;
        double personY = tubeY + tubeHeight - personHeight;

        Color headColor = Color.LIGHTGRAY;
        Color bodyColor = Color.WHITE;
        if (lw.getAuthor() != null && lw.getAuthor().getEyeColor() != null) {
            headColor = switch (lw.getAuthor().getEyeColor()) {
                case GREEN  -> Color.web("#32CD32");
                case YELLOW -> Color.web("#FFD700");
                case BROWN  -> Color.SADDLEBROWN;
                case RED    -> Color.CRIMSON;
                case BLACK  -> Color.BLACK;
                case BLUE   -> Color.DODGERBLUE;
            };
        }

        double headRadius = personWidth * 0.5;
        double bodyX = personX;
        double bodyY = personY + headRadius * 2;
        double bodyW = personWidth;
        double bodyH = personHeight - headRadius * 2;
        gc.setFill(bodyColor);
        gc.fillRect(bodyX, bodyY, bodyW, bodyH);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(bodyX, bodyY, bodyW, bodyH);

        double headCenterX = personX + headRadius;
        double headCenterY = personY + headRadius;
        gc.setFill(headColor);
        gc.fillOval(headCenterX - headRadius, headCenterY - headRadius,
                headRadius * 2, headRadius * 2);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeOval(headCenterX - headRadius, headCenterY - headRadius,
                headRadius * 2, headRadius * 2);

        Color hatColor = Color.SIENNA;
        if (lw.getAuthor() != null && lw.getAuthor().getHairColor() != null) {
            hatColor = switch (lw.getAuthor().getHairColor()) {
                case GREEN  -> Color.web("#228B22");
                case YELLOW -> Color.web("#FFFACD");
                case BROWN  -> Color.SIENNA;
                case RED    -> Color.FIREBRICK;
                case BLACK  -> Color.DIMGRAY;
                case BLUE   -> Color.ROYALBLUE;
            };
        }

        double hatWidth   = personWidth + 10;
        double hatBrimH   = 6;
        double hatCrownH  = 12;
        double hatX       = personX - ((hatWidth - personWidth) / 2.0);
        double hatY       = headCenterY - headRadius - (hatBrimH / 2.0);

        gc.setFill(hatColor);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.fillOval(hatX, hatY, hatWidth, hatBrimH);
        gc.strokeOval(hatX, hatY, hatWidth, hatBrimH);

        double crownW = personWidth + ((hatWidth - personWidth) / 2.0);
        double crownX = hatX + ((hatWidth - personWidth) / 4.0);
        double crownY = hatY - hatCrownH + (hatBrimH / 2.0);
        double crownH = hatCrownH;
        gc.fillRoundRect(crownX, crownY, crownW, crownH, crownH / 2.0, crownH / 2.0);
        gc.strokeRoundRect(crownX, crownY, crownW, crownH, crownH / 2.0, crownH / 2.0);
    }

    private void showEditDialog(LabWork existing) {
        LabWorkDialog dlg = new LabWorkDialog(existing, bundle);
        dlg.showAndWait().ifPresent(updated -> {
            boolean ok = (existing == null)
                    ? serverApi.add(updated)
                    : serverApi.update(existing.getId(), updated);

            if (ok) {
                statusLabel.setText(
                        existing == null
                                ? bundle.getString("status.added")
                                : bundle.getString("status.updated")
                );
            } else {
                statusLabel.setText(
                        existing == null
                                ? bundle.getString("status.add_failed")
                                : bundle.getString("status.update_failed")
                );
            }
            loadDataFromServer();
        });
    }

    @FXML
    private void onAdd()          { showEditDialog(null); }
    @FXML
    private void onUpdate() {
        LabWork sel = tableView.getSelectionModel().getSelectedItem();
        if (sel != null) showEditDialog(sel);
    }
    @FXML
    private void onRemove() {
        LabWork sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        boolean ok = serverApi.remove(sel.getId());
        if (ok) statusLabel.setText(bundle.getString("status.removed"));
        else    statusLabel.setText(bundle.getString("status.remove_failed"));
        loadDataFromServer();
    }
    @FXML
    private void onRefresh() {
        loadDataFromServer();
        statusLabel.setText(bundle.getString("status.refreshed"));
    }
    @FXML
    private void onInfo()    { statusLabel.setText(serverApi.info()); }
    @FXML
    private void onClear() {
        boolean ok = serverApi.clear();
        if (ok) statusLabel.setText(bundle.getString("status.cleared"));
        else    statusLabel.setText(bundle.getString("status.clear_failed"));
        loadDataFromServer();
    }
    @FXML
    private void onAddIfMax() {
        LabWorkDialog dlg = new LabWorkDialog(null, bundle);
        dlg.showAndWait().ifPresent(lw -> {
            boolean ok = serverApi.addIfMax(lw);
            if (ok) statusLabel.setText(bundle.getString("status.add_if_max"));
            else    statusLabel.setText(bundle.getString("status.add_if_max_failed"));
            loadDataFromServer();
        });
    }
    @FXML
    private void onRemoveById() {
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle(bundle.getString("cmd.remove_by_id"));
        tid.setHeaderText(null);
        tid.setContentText(bundle.getString("prompt.enter_id"));
        tid.showAndWait().ifPresent(s -> {
            try {
                long id = Long.parseLong(s.trim());
                boolean ok = serverApi.remove(id);
                if (ok) statusLabel.setText(bundle.getString("status.removed"));
                else    statusLabel.setText(bundle.getString("status.remove_failed"));
            } catch (NumberFormatException ex) {
                statusLabel.setText(bundle.getString("error.invalid_id"));
            }
            loadDataFromServer();
        });
    }
    @FXML
    private void onCountLessThanDifficulty() {
        ChoiceDialog<String> cd = new ChoiceDialog<>("EASY", "EASY", "HARD", "INSANE");
        cd.setTitle(bundle.getString("cmd.count_less_than_diff"));
        cd.setHeaderText(null);
        cd.setContentText(bundle.getString("prompt.select_difficulty"));
        cd.showAndWait().ifPresent(d -> {
            long cnt = serverApi.countLessThanDifficulty(d);
            statusLabel.setText(bundle.getString("status.count_less_than_diff") + cnt);
        });
    }
    @FXML
    private void onAverageOfMinimalPoint() {
        double avg = serverApi.averageOfMinimalPoint();
        statusLabel.setText(bundle.getString("status.average_minimal_point") + avg);
    }
    @FXML
    private void onMinById() {
        LabWork lw = serverApi.minById();
        if (lw != null) {
            tableView.getSelectionModel().select(lw);
            tableView.scrollTo(lw);
            statusLabel.setText(bundle.getString("status.min_by_id"));
        } else {
            statusLabel.setText(bundle.getString("status.min_by_id_failed"));
        }
    }
    @FXML
    private void onExecuteScript() {
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle(bundle.getString("cmd.execute_script"));
        tid.setHeaderText(null);
        tid.setContentText(bundle.getString("prompt.enter_script_file"));
        tid.showAndWait().ifPresent(fn -> {
            String out = serverApi.executeScript(fn.trim());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("cmd.execute_script"));
            TextArea ta = new TextArea(out);
            ta.setEditable(false);
            ta.setWrapText(true);
            alert.getDialogPane().setContent(ta);
            alert.showAndWait();
            statusLabel.setText(bundle.getString("status.script_executed"));
        });
    }
    @FXML
    private void onLogout() {
        if (pollingTimeline != null) pollingTimeline.stop();
        if (scatterPulseTimeline != null) scatterPulseTimeline.stop();
        stopDetailAnimation();

        currentUserMenuItem.getParentPopup().getScene().getWindow().fireEvent(
                new WindowEvent(
                        currentUserMenuItem.getParentPopup().getScene().getWindow(),
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
        MainApp.showLogin();
    }

    private SimpleStringProperty bundleProperty(String key) {
        return new SimpleStringProperty(bundle.getString(key));
    }
}
