
package controlpanel;

/*
 * PrinterQueue get's new Orders from the Central Dashboard
 *
 */

public class PrinterQueue {

    private String[] constructionSteps;
    private String displayName;

    private int currentTaskIndex = 0;

    public PrinterQueue(String displayName) {
        this.displayName = displayName;
    }

    public String getNextTask() {
        if(this.currentTaskIndex < this.constructionSteps.length) {
            return this.constructionSteps[this.currentTaskIndex];
        }
        else {
            return "{\"type\": \"success\", \"message\": \"PrinterQueue is currently empty.\"}";
        }
    }


}