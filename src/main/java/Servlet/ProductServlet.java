package Servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.IProductService;
import org.example.Product;
import org.example.ProductService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
    private final IProductService productService = new ProductService();
    private Gson gson = new Gson();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         Product product = gson.fromJson(readRequestBody(request),Product.class);
         productService.addProduct(product);
         response.setContentType("application/json");
        response.getWriter().write(gson.toJson(product));

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          String pathInfo = request.getPathInfo();
           response.setContentType("application/json");
           if(pathInfo == null || pathInfo.equals("/")){
               List<Product>products = productService.getProducts();
               response.getWriter().write(gson.toJson(products));
           }else {
               try {
                   int productId = Integer.parseInt(pathInfo.substring(1));
                   Product product = productService.getProductById(productId);
                   if(product!=null){
                       response.getWriter().write(gson.toJson(product));
                   }else{
                       response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                       response.getWriter().write("Product not found");
                   }
               }catch (NumberFormatException e){
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().write("Invalid ID format");
               }
           }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID is required in the URL");
            return;
        }

        try {
            int productId = Integer.parseInt(pathInfo.substring(1));
            Product existing = productService.getProductById(productId);

            if (existing == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Product not found");
                return;
            }

            Product updatedProduct = gson.fromJson(readRequestBody(request), Product.class);
            updatedProduct.setId(productId);
            productService.updateProduct(updatedProduct);
            response.getWriter().write(gson.toJson(updatedProduct));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid ID format");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         String pathInfo = request.getPathInfo();
         if(pathInfo != null && pathInfo.length()>1){
             try {
                 int productId = Integer.parseInt(pathInfo.substring(1));
                 boolean removed = productService.deleteProduct(productId);
                 if(removed){
                     response.getWriter().write("Product deleted successfully");
                 }else{
                     response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                     response.getWriter().write("Product not found");
                 }
             }catch (NumberFormatException e){
                 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                 response.getWriter().write("Invalid ID format");

             }
         }else {
             response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             response.getWriter().write("ID is required");
         }

    }


    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line=reader.readLine())!=null){
            stringBuilder.append(line);
        }
           return stringBuilder.toString();
    }
}
