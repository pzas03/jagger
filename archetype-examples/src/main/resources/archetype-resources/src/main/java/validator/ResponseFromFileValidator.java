#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.exception.TechnicalException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class ResponseFromFileValidator<Q, E, R> extends ResponseValidator<Q, E, R> {


    private String filePath= "suite/validatorResources/response.txt";
    private String expectedResponse;

    public ResponseFromFileValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseFromFileValidator";
    }

    private void initiate(){
        StringBuilder sb = new StringBuilder();
        String fs = System.getProperty("line.separator");
        Scanner scanner=null;
        try {
            scanner= new Scanner(new FileInputStream(filePath));
            while (scanner.hasNextLine()){
                sb.append(scanner.nextLine() + fs);
            }
        } catch (FileNotFoundException e) {
            throw new TechnicalException("Error during read file ", e);
        } finally{
            if(scanner!=null){
                scanner.close();
            }
        }
        expectedResponse=sb.toString();
    }

    @Override
    public boolean validate(Q query, E endpoint, R result, long duration) {
        if(expectedResponse==null){
            synchronized (filePath){
                if(expectedResponse==null){
                    initiate();
                }
            }
        }
        return expectedResponse.equals(result);
    }
}
