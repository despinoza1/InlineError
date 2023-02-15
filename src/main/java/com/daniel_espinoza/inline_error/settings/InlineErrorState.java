package com.daniel_espinoza.inline_error.settings;

import com.daniel_espinoza.inline_error.InlineError;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.daniel_espinoza.inline_errors.InlineErrorsState",
        storages = {@Storage("InlineErrorPluginSettings.xml")}
)
public class InlineErrorState implements PersistentStateComponent<InlineErrorState> {
    public String highlightSeverity = "None";
    public int highlightErrorColor = 4862772;
    public int errorTextColor = 16737637;

    public int highlightWarnColor = 8802592;
    public int warnTextColor = 16749615;

    public int highlightInfoColor = 225162;
    public int infoTextColor = 47076;
    public String collector = InlineError.PROBLEMS();

    public String severity = "WARN";

    public static InlineErrorState getInstance() {
        if (ApplicationManager.getApplication().isDisposed()) return null;

        return ApplicationManager.getApplication().getService(InlineErrorState.class);
    }

    @Nullable
    @Override
    public InlineErrorState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull InlineErrorState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
