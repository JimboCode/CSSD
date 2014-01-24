package UI;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.MenuComponent;
import java.awt.event.MouseEvent;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

/**
 * Base class to provide modal like behaviour to JInternalFrame classes
 * 
 * @author James Staite
 */
public class ModalBase extends JInternalFrame
{
    // behaviour flag
    boolean modal = false;

    /**
     * Constructor to extend the base class
     * @param title Title of the window
     * @param resizeable if can be resized
     * @param closable if closable
     * @param maximizable if window can be maximised
     * @param iconifable if window can be minimised
     */
    public ModalBase(String title, boolean resizeable, boolean closable, boolean maximizable, boolean iconifable)
    {
        // pass call to base class
        super(title, resizeable, closable, maximizable, iconifable);
    }
    
    /**
     * override show method to invoke the extra method to do the necessary additional work
     * to make the window modal
     */
    @Override
    public void show() 
    {
        super.show();
        if (this.modal) startModal();
    }

    /**
     * overrides the to turn on and off the mock modal behaviour
     * @param value value
     */
    @Override
    public void setVisible(boolean value) 
    {
        super.setVisible(value);
        if (modal) 
        {
            if (value) startModal(); else stopModal();    
        }
    }

    /**
     * sets up the mock modal behaviour
     */
    private synchronized void startModal() 
    {
        try {
            if (SwingUtilities.isEventDispatchThread()) 
            {
                EventQueue theQueue = getToolkit().getSystemEventQueue();
                while (isVisible()) 
                {
                    AWTEvent event = theQueue.getNextEvent();
                    Object source = event.getSource();
                    boolean dispatch = true;

                    if (event instanceof MouseEvent) 
                    {
                        MouseEvent e = (MouseEvent) event;
                        MouseEvent m = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, this);
                        if (!this.contains(m.getPoint()) && e.getID() != MouseEvent.MOUSE_DRAGGED) 
                        {
                            dispatch = false;
                        }
                    }

                    if (dispatch) 
                    {
                        if (event instanceof ActiveEvent) 
                        {
                            ((ActiveEvent) event).dispatch();
                        }
                        else if (source instanceof Component) 
                        {
                            ((Component) source).dispatchEvent(event);
                        } 
                        else if (source instanceof MenuComponent) 
                        {
                            ((MenuComponent) source).dispatchEvent(event);
                        } 
                        else 
                        {
                            System.err.println("Unable to dispatch: " + event);
                        }
                    }
                }
            } 
            else
            {
                while (isVisible()) 
                {
                    wait();
                }
            }
        } 
        catch (InterruptedException ignored) {}
    }

    /**
     * Raise event when modal behaviour is turn off
     */
    private synchronized void stopModal() {
        notifyAll();
    }

    /**
     * set the modal behaviour of the form
     * @param modal on - true / off - false
     */
    public void setModal(boolean modal) {
        this.modal = modal;
    }

    /**
     * Return the current modal state of the form
     * @return boolean answer
     */
    public boolean isModal() {
        return this.modal;
    }

}
