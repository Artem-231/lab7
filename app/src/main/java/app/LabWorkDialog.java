package app;

import core.enums.Color;
import core.enums.Country;
import core.enums.Difficulty;
import core.objects.Coordinates;
import core.objects.LabWork;
import core.objects.Person;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ResourceBundle;

public class LabWorkDialog extends Dialog<LabWork> {
    private final TextField nameField       = new TextField();
    private final TextField xField          = new TextField();
    private final TextField yField          = new TextField();
    private final TextField minPointField   = new TextField();
    private final TextArea  descriptionArea = new TextArea();
    private final ComboBox<Difficulty> difficultyCombo     = new ComboBox<>();
    private final TextField authorNameField   = new TextField();
    private final TextField authorWeightField = new TextField();
    private final ComboBox<Color>  eyeColorCombo   = new ComboBox<>();
    private final ComboBox<Color>  hairColorCombo  = new ComboBox<>();
    private final ComboBox<Country> nationalityCombo = new ComboBox<>();

    public LabWorkDialog(LabWork existing, ResourceBundle bundle) {
        setTitle(existing == null
                ? bundle.getString("dialog.add.title")
                : bundle.getString("dialog.update.title"));
        getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        difficultyCombo.getItems().setAll(Difficulty.values());
        eyeColorCombo.getItems() .setAll(Color.values());
        hairColorCombo.getItems().setAll(Color.values());
        nationalityCombo.getItems().setAll(Country.values());

        // Заполнение при update
        if (existing != null) {
            nameField.setText(existing.getName());
            xField.setText(String.valueOf(existing.getCoordinates().getX()));
            yField.setText(String.valueOf(existing.getCoordinates().getY()));
            minPointField.setText(String.valueOf(existing.getMinimalPoint()));
            descriptionArea.setText(existing.getDescription());
            difficultyCombo.setValue(existing.getDifficulty());
            Person a = existing.getAuthor();
            authorNameField .setText(a.getName());
            authorWeightField.setText(String.valueOf(a.getWeight()));
            eyeColorCombo .setValue(a.getEyeColor());
            hairColorCombo.setValue(a.getHairColor());
            nationalityCombo.setValue(a.getNationality());
        }

        descriptionArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20,150,10,10));

        int row=0;
        grid.add(new Label(bundle.getString("col.name")+":"),0,row);
        grid.add(nameField,1,row++);
        grid.add(new Label(bundle.getString("col.x")+": (число)"),0,row);
        grid.add(xField,1,row++);
        grid.add(new Label(bundle.getString("col.y")+": (целое)"),0,row);
        grid.add(yField,1,row++);
        grid.add(new Label(bundle.getString("col.minimalPoint")+": (>0)"),0,row);
        grid.add(minPointField,1,row++);
        grid.add(new Label(bundle.getString("col.description")+": (не пусто)"),0,row);
        grid.add(descriptionArea,1,row++);
        grid.add(new Label(bundle.getString("col.difficulty")+":"),0,row);
        grid.add(difficultyCombo,1,row++);
        grid.add(new Separator(),0,row++,2,1);
        grid.add(new Label(bundle.getString("author.section")),0,row++,2,1);
        grid.add(new Label(bundle.getString("author.name")+": (не пусто)"),0,row);
        grid.add(authorNameField,1,row++);
        grid.add(new Label(bundle.getString("author.weight")+": (>0)"),0,row);
        grid.add(authorWeightField,1,row++);
        grid.add(new Label(bundle.getString("author.eye_color")+":"),0,row);
        grid.add(eyeColorCombo,1,row++);
        grid.add(new Label(bundle.getString("author.hair_color")+":"),0,row);
        grid.add(hairColorCombo,1,row++);
        grid.add(new Label(bundle.getString("author.nationality")+":"),0,row);
        grid.add(nationalityCombo,1,row++);

        getDialogPane().setContent(grid);

        // Валидация
        Runnable validate = this::validateForm;
        nameField.textProperty()        .addListener((o,v,n)->validate.run());
        xField.textProperty()           .addListener((o,v,n)->validate.run());
        yField.textProperty()           .addListener((o,v,n)->validate.run());
        minPointField.textProperty()    .addListener((o,v,n)->validate.run());
        descriptionArea.textProperty()  .addListener((o,v,n)->validate.run());
        difficultyCombo.valueProperty() .addListener((o,v,n)->validate.run());
        authorNameField.textProperty()  .addListener((o,v,n)->validate.run());
        authorWeightField.textProperty().addListener((o,v,n)->validate.run());
        hairColorCombo.valueProperty()  .addListener((o,v,n)->validate.run());
        nationalityCombo.valueProperty().addListener((o,v,n)->validate.run());
        validate.run();

        // Результат
        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String name  = nameField.getText().trim();
                double x     = Double.parseDouble(xField.getText().trim());
                long   y     = Long.parseLong(yField.getText().trim());
                float  mp    = Float.parseFloat(minPointField.getText().trim());
                String desc  = descriptionArea.getText().trim();
                Difficulty diff = difficultyCombo.getValue();
                Person author = new Person(
                        authorNameField.getText().trim(),
                        Integer.parseInt(authorWeightField.getText().trim()),
                        eyeColorCombo.getValue(),
                        hairColorCombo .getValue(),
                        nationalityCombo.getValue()
                );

                if (existing == null) {
                    // новый
                    return new LabWork(
                            name,
                            new Coordinates(x,y),
                            mp,
                            desc,
                            diff,
                            author
                    );
                } else {
                    // копия + обновление
                    LabWork copy = new LabWork(existing);
                    copy.setName(name);
                    copy.setCoordinates(new Coordinates(x,y));
                    copy.setMinimalPoint(mp);
                    copy.setDescription(desc);
                    copy.setDifficulty(diff);
                    copy.setAuthor(author);
                    return copy;
                }
            }
            return null;
        });
    }

    private void validateForm() {
        boolean ok = true;
        if (nameField.getText().trim().isEmpty()) ok=false;
        try { Double.parseDouble(xField.getText().trim()); }
        catch(Exception e){ ok=false; }
        try { Long.parseLong(yField.getText().trim()); }
        catch(Exception e){ ok=false; }
        try {
            float v = Float.parseFloat(minPointField.getText().trim());
            if (v<=0) ok=false;
        } catch(Exception e){ ok=false; }
        if (descriptionArea.getText().trim().isEmpty()) ok=false;
        if (difficultyCombo.getValue()==null) ok=false;
        if (authorNameField.getText().trim().isEmpty()) ok=false;
        try { if(Integer.parseInt(authorWeightField.getText().trim())<=0) ok=false; }
        catch(Exception e){ ok=false; }
        if (hairColorCombo.getValue()==null || nationalityCombo.getValue()==null) ok=false;

        Node okBtn = getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setDisable(!ok);
    }
}
