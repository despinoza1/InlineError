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
    public boolean isEnabled = true;
    public boolean highlightIsEnabled = false;
    public int highlightColor = 4862772;
    public int textColor = 16737637;
    public String collector = InlineError.PROBLEMS();

    public static InlineErrorState getInstance() {
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
