package org.camokatuk.madcomposer.ui;

import org.camokatuk.madcomposer.engine.control.EngineControlRoom;
import org.camokatuk.madcomposer.ui.playback.BpmChangeListener;
import org.camokatuk.madcomposer.ui.playback.TestListener;

import javax.sound.midi.MidiDevice;
import javax.swing.*;
import java.util.Collection;

public class MainFrame extends JFrame
{
    private final EngineControlRoom controlRoom;

    public MainFrame(EngineControlRoom controlRoom)
    {
        this.controlRoom = controlRoom;
        this.initialize();
    }

    private void initialize()
    {
        this.addPlaybackControls(controlRoom);
        this.pack();
    }

    private void addPlaybackControls(EngineControlRoom controlRoom)
    {
        final JToolBar toolBar = new JToolBar();

        toolBar.add(this.constructDeviceSelector());

        JTextField textField = new JTextField(Integer.toString(controlRoom.getStateMonitor().getBpm()), 3);
        textField.getDocument().addDocumentListener(new BpmChangeListener(controlRoom));
        toolBar.add(textField);

        JButton button = new JButton("Test");
        button.addActionListener(new TestListener(controlRoom));
        toolBar.add(button);

        this.getContentPane().add(toolBar);
    }

    private JComboBox constructDeviceSelector()
    {
        Collection<MidiDevice> devices = controlRoom.getMidiSpammerController().listMidiDevices();
        JComboBox<MidiDevice> selector = new JComboBox<>(devices.toArray(new MidiDevice[devices.size()]));
        selector.setSelectedItem(controlRoom.getMidiSpammerController().getDefaultDevice());

        DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
        selector.setRenderer((list, value, index, isSelected, cellHasFocus) -> defaultListCellRenderer.getListCellRendererComponent(list, value.getDeviceInfo().getName(), index, isSelected, cellHasFocus));
        selector.addActionListener(e -> {
            controlRoom.getMidiSpammerController().switchDeviceTo((MidiDevice) selector.getSelectedItem());
        });
        return selector;
    }
}
