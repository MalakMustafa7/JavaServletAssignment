package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.IProductService;
import org.example.Product;
import org.example.ProductService;

import java.io.IOException;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private final IProductService productService = new ProductService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Product product = getProductFromRequest(request);
        productService.addProduct(product);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
           response.setContentType("text/plain");
           String idParam = request.getParameter("id");
           if(idParam !=null){
               int productId = Integer.parseInt(idParam);
               Product product = productService.getProductById(productId);
               if(product !=null){
                   response.getWriter() .println(product);
               }else{
                   response.getWriter().println("Product Not Found");
               }
           }else {
               for (Product product : productService.getProducts()) {
                   response.getWriter().println(product);
               }
           }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         Product product = getProductFromRequest(request);
        productService.updateProduct(product);
        doGet(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        productService.deleteProduct(id);
        response.setContentType("text/plain");
        response.getWriter().println("Product with id: "+id+" deleted successfully");
    }

    private Product getProductFromRequest(HttpServletRequest request){
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int id = Integer.parseInt(request.getParameter("id"));
        return new Product(id,name,price);
    }
}
