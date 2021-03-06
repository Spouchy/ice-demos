//
// Copyright (c) ZeroC, Inc. All rights reserved.
//

public class PrinterI extends Ice.Blobject
{
    @Override
    public boolean
    ice_invoke(byte[] inParams, Ice.ByteSeqHolder outParams, Ice.Current current)
    {
        Ice.Communicator communicator = current.adapter.getCommunicator();

        boolean result = true;
        Ice.InputStream in = new Ice.InputStream(communicator, inParams);
        in.startEncapsulation();

        if(current.operation.equals("printString"))
        {
            String message = in.readString();
            System.out.println("Printing string `" + message + "'");
        }
        else if(current.operation.equals("printStringSequence"))
        {
            String[] seq = Demo.StringSeqHelper.read(in);
            System.out.print("Printing string sequence {");
            for(int i = 0; i < seq.length; ++i)
            {
                if(i > 0)
                {
                    System.out.print(", ");
                }
                System.out.print("'" + seq[i] + "'");
            }
            System.out.println("}");
        }
        else if(current.operation.equals("printDictionary"))
        {
            java.util.Map<String, String> dict = Demo.StringDictHelper.read(in);
            System.out.print("Printing dictionary {");
            boolean first = true;
            for(java.util.Map.Entry<String, String> i : dict.entrySet())
            {
                if(!first)
                {
                    System.out.print(", ");
                }
                first = false;
                System.out.print(i.getKey() + "=" + i.getValue());
            }
            System.out.println("}");
        }
        else if(current.operation.equals("printEnum"))
        {
            Demo.Color c = Demo.Color.ice_read(in);
            System.out.println("Printing enum " + c);
        }
        else if(current.operation.equals("printStruct"))
        {
            Demo.Structure s = Demo.Structure.ice_read(in);
            System.out.println("Printing struct: name=" + s.name + ", value=" + s.value);
        }
        else if(current.operation.equals("printStructSequence"))
        {
            Demo.Structure[] seq = Demo.StructureSeqHelper.read(in);
            System.out.print("Printing struct sequence: {");
            for(int i = 0; i < seq.length; ++i)
            {
                if(i > 0)
                {
                    System.out.print(", ");
                }
                System.out.print(seq[i].name + "=" + seq[i].value);
            }
            System.out.println("}");
        }
        else if(current.operation.equals("printClass"))
        {
            Demo.CHolder c = new Demo.CHolder();
            in.readValue(c);
            in.readPendingValues();
            System.out.println("Printing class: s.name=" + c.value.s.name + ", s.value=" + c.value.s.value);
        }
        else if(current.operation.equals("getValues"))
        {
            Demo.C c = new Demo.C();
            c.s = new Demo.Structure();
            c.s.name = "green";
            c.s.value = Demo.Color.green;
            Ice.OutputStream out = new Ice.OutputStream(communicator);
            out.startEncapsulation();
            out.writeValue(c);
            out.writeString("hello");
            out.writePendingValues();
            out.endEncapsulation();
            outParams.value = out.finished();
        }
        else if(current.operation.equals("throwPrintFailure"))
        {
            System.out.println("Throwing PrintFailure");
            Demo.PrintFailure ex = new Demo.PrintFailure();
            ex.reason = "paper tray empty";
            Ice.OutputStream out = new Ice.OutputStream(communicator);
            out.startEncapsulation();
            out.writeException(ex);
            out.endEncapsulation();
            outParams.value = out.finished();
            result = false;
        }
        else if(current.operation.equals("shutdown"))
        {
            current.adapter.getCommunicator().shutdown();
        }
        else
        {
            Ice.OperationNotExistException ex = new Ice.OperationNotExistException();
            ex.id = current.id;
            ex.facet = current.facet;
            ex.operation = current.operation;
            throw ex;
        }

        in.endEncapsulation();
        return result;
    }
}
