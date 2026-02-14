import java.net.*;
        import java.io.*;
        class MulServer
        {
          public static void main(String args[]) throws IOException
          { ServerSocket server = new ServerSocket(55555);
              while(true){
              //es wartet so lange bis etwas ankommt, wegen dem accept.
            Socket client = server.accept();

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

                int start = in.read();
                int end = in.read();
                int result = start * end;
                out.write(result);
            }
          }
        }

