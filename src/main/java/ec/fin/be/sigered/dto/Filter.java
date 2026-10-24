package ec.fin.be.sigered.dto;

/**
 *
 * @author pmontenegro
 */
public class Filter {
    
   private String filterString;
   
   public Filter() {
   }
    
   public Filter(String filterString) {
       this.filterString = filterString;
   }
    
   public String getFilterString() {
       return filterString;
   }

   public void setFilterString(String filterString) {
       this.filterString = filterString;
   }

    
}
