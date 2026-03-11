package cn.luojunhui.touchfish.actions;

import cn.luojunhui.touchfish.config.BookSettingsState;
import cn.luojunhui.touchfish.windwos.Book;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

/**
 * 切换自动翻页开关，默认快捷键 Ctrl+Caps Lock。
 */
public class ToggleAutoPageTurnAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BookSettingsState settings = BookSettingsState.getInstance();
        BookSettingsState state = settings.getState();
        if (state != null) {
            state.setAutoPageTurnEnabled(!state.getAutoPageTurnEnabled());
            settings.loadState(state);
        }
        Book.applyAutoPageTurnState();
    }
}
