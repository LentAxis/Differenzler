
package ch.ntb.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SceneChanger {
    
    public enum View {CONNECTION, MAIN}

    private final ObjectProperty<View> currentView = new SimpleObjectProperty<>(View.CONNECTION);

    public ObjectProperty<View> currentViewProperty() {
        return currentView ;
    }

    public final View getCurrentView() {
        return currentViewProperty().get();
    }

    public final void setCurrentView(View view) {
        currentViewProperty().set(view);
    }
   
}
