import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;


/**
 * @author Antonis_Kotsis
 *
 */

public class A_star {

    final int ARRAY_SIZE=10;
    final int INIT_PAR=-1;
    final int MAX_VAL=1000000;
    int [][] grid=new int [ARRAY_SIZE][ARRAY_SIZE];
    //stacks for storing the path of each destination
    Stack<Cell> path1=new Stack<Cell>();
    Stack<Cell> path2=new Stack<Cell>();
    /*
    @isValidPos(Cell inCell)
    --Checks if the cell's position that user gave is valid (part of the grid)
     */
    private boolean isValid(int row,int col) {
        if((row>=0) && (row<ARRAY_SIZE) && (col>=0) && col<ARRAY_SIZE) {
            return true;
        }
        return false;
    }

    /*
    @initializeGrid(int grid[ARRAY_SIZE][ARRAY_SIZE])
    --initializes randomly the grid only using 1(=free cell) and 0(=blocked cell)
     */
    public void initializeGrid(int [][] grid) {
        for(int i=0;i<ARRAY_SIZE;i++) {
            for(int j=0;j<ARRAY_SIZE;j++) {
                grid[i][j]=(int) (Math.random() *2);
            }
        }
    }

    /*
    @isEmptyCell(Cell cell)
    --returns true if the cell is empty(we can access it) else returns false
     */
    boolean isEmptyCell(int row,int col,int [][] grid) {
        if(grid[row][col]==1) {
            //empty cell
            return true;
        }
        return false;
    }



    void printGrid(int [][] grid) {
        for(int i=0;i<ARRAY_SIZE;i++) {
            for(int j=0;j<ARRAY_SIZE;j++) {
                System.out.print(grid[i][j]+" ");
            }
            System.out.println();
        }
    }

    /*
    @computeH(int row ,int col,Cell dest)
    --calculates the h value for each cell
    --we used Diagonal Distance heuristic function cause our program can move to all 8 available directions
    */
    int computeH(int row,int col,Cell dest) {
        int h;
        h=Math.max(Math.abs(row-dest.row), Math.abs(col-dest.col));
        return h;
    }

    /*
    @a_star(int grid[ARRAY_SIZE][ARRAY_SIZE],Cell start,Cell dest)
    --executes a* algorithm
    */
    int a_star(int [][] grid,Cell start,Cell dest,int destNum) {
        //initialize the closed list to false which means there is nothing inside
        //when we add a cell into closedList then we change the value of the specific cell to true
        //when we finish proccessing a cell we add that in closedList
        boolean [][] closedList=new boolean[ARRAY_SIZE][ARRAY_SIZE];
        //contains the f,g,h values of a grid cell and also contains the position of a cell's parent
        CellValues [][]cell_values=new CellValues[ARRAY_SIZE][ARRAY_SIZE];
        //open list contains all the cell that we are going to process
        ArrayList<openListCell> openList=new ArrayList<openListCell>();
        //make it true when we find the dest cell
        boolean reachedDest=false;

        int nf,ng,nh;
        int io,jo;
        int total_cost=0;


        if(destReached(start.row,start.col,dest)){
            System.out.println("Source cell is the destination ");
            return -1;
        }

        for (int i = 0; i < ARRAY_SIZE; i++) {
            for (int j = 0; j < ARRAY_SIZE; j++) {
                CellValues init=new CellValues(INIT_PAR,INIT_PAR,MAX_VAL,MAX_VAL,MAX_VAL);

                cell_values[i][j]=init;
                //nothing in the closed list yet
                closedList[i][j]=false;
            }
        }




        //initialize cell_values for the start cell
        //start cell is the parent of it's own

        cell_values[start.row][start.col].f=0;
        cell_values[start.row][start.col].g=0;
        cell_values[start.row][start.col].h=0;
        cell_values[start.row][start.col].par_row=start.row;
        cell_values[start.row][start.col].par_col=start.col;



        //insert the starting cell to the openList
        openListCell startOpenCell=new openListCell(0,start.row,start.col);
        openList.add(startOpenCell);
        int number_of_ext=0;
        while(!openList.isEmpty()) {
            openListCell olc=openList.get(0);
            //when we start processing a cell we remove it from the openList
            openList.remove(0);
            //add this cell to the closedList
            io=olc.row;jo=olc.col;
            closedList[io][jo]=true;

            //TOP of the cell
            if(isValid(io-1,jo)) {
                number_of_ext++;
                if(destReached(io-1,jo,dest)) {
                    cell_values[io-1][jo].par_row=io;
                    cell_values[io-1][jo].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost=pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor is in the closed list(already visited) then skip it else do the following
                else if(closedList[io-1][jo]==false && isEmptyCell(io-1,jo,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io-1,jo,dest);
                    nf=ng+nh;

                    //notInOpenList(new openListCell(cell_values[io-1][jo].f,io-1,jo),openList)
                    if(cell_values[io-1][jo].f>nf || cell_values[io-1][jo].f==MAX_VAL) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io-1,jo));

                        cell_values[io-1][jo].f=nf;
                        cell_values[io-1][jo].g=ng;
                        cell_values[io-1][jo].h=nh;
                        cell_values[io-1][jo].par_row=io;
                        cell_values[io-1][jo].par_col=jo;
                    }
                }

            }
            //BOTTOM of the cell
            if(isValid(io+1,jo)) {
                number_of_ext++;
                if(destReached(io+1,jo,dest)) {
                    cell_values[io+1][jo].par_row=io;
                    cell_values[io+1][jo].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost= pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io+1][jo]==false && isEmptyCell(io+1,jo,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io+1,jo,dest);
                    nf=ng+nh;


                    if(cell_values[io+1][jo].f>nf ||cell_values[io+1][jo].f==MAX_VAL) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io+1,jo));

                        cell_values[io+1][jo].f=nf;
                        cell_values[io+1][jo].g=ng;
                        cell_values[io+1][jo].h=nh;
                        cell_values[io+1][jo].par_row=io;
                        cell_values[io+1][jo].par_col=jo;
                    }
                }

            }

            //RIGHT of the cell
            if(isValid(io,jo+1)) {
                number_of_ext++;

                if(destReached(io,jo+1,dest)) {
                    cell_values[io][jo+1].par_row=io;
                    cell_values[io][jo+1].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost=pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io][jo+1]==false && isEmptyCell(io,jo+1,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io,jo+1,dest);
                    nf=ng+nh;

                    //notInOpenList(new openListCell(cell_values[io][jo+1].f,io,jo+1),openList)
                    if(cell_values[io][jo+1].f>nf||cell_values[io][jo+1].f==MAX_VAL ) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io,jo+1));

                        cell_values[io][jo+1].f=nf;
                        cell_values[io][jo+1].g=ng;
                        cell_values[io][jo+1].h=nh;
                        cell_values[io][jo+1].par_row=io;
                        cell_values[io][jo+1].par_col=jo;
                    }
                }

            }

            //LEFT of the cell
            if(isValid(io,jo-1)) {
                number_of_ext++;

                if(destReached(io,jo-1,dest)) {
                    cell_values[io][jo-1].par_row=io;
                    cell_values[io][jo-1].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost= pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io][jo-1]==false && isEmptyCell(io,jo-1,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io,jo-1,dest);
                    nf=ng+nh;

                    //notInOpenList(new openListCell(cell_values[io][jo-1].f,io,jo-1),openList)
                    if(cell_values[io][jo-1].f>nf||cell_values[io][jo-1].f==MAX_VAL ) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io,jo-1));

                        cell_values[io][jo-1].f=nf;
                        cell_values[io][jo-1].g=ng;
                        cell_values[io][jo-1].h=nh;
                        cell_values[io][jo-1].par_row=io;
                        cell_values[io][jo-1].par_col=jo;
                    }
                }

            }

            //UPPER_RIGHT DIAGONAL
            if(isValid(io-1,jo+1)) {
                number_of_ext++;

                if(destReached(io-1,jo+1,dest)) {
                    cell_values[io-1][jo+1].par_row=io;
                    cell_values[io-1][jo+1].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost= pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io-1][jo+1]==false && isEmptyCell(io-1,jo+1,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io-1,jo+1,dest);
                    nf=ng+nh;

                    //notInOpenList(new openListCell(cell_values[io-1][jo+1].f,io-1,jo+1),openList)
                    if(cell_values[io-1][jo+1].f>nf|| cell_values[io-1][jo+1].f==MAX_VAL) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io-1,jo+1));

                        cell_values[io-1][jo+1].f=nf;
                        cell_values[io-1][jo+1].g=ng;
                        cell_values[io-1][jo+1].h=nh;
                        cell_values[io-1][jo+1].par_row=io;
                        cell_values[io-1][jo+1].par_col=jo;
                    }
                }

            }

            //UPPER_LEFT DIAGONAL
            if(isValid(io-1,jo-1)) {
                number_of_ext++;

                if(destReached(io-1,jo-1,dest)) {
                    cell_values[io-1][jo-1].par_row=io;
                    cell_values[io-1][jo-1].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost= pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io-1][jo-1]==false && isEmptyCell(io-1,jo-1,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io-1,jo-1,dest);
                    nf=ng+nh;

                    // notInOpenList(new openListCell(cell_values[io-1][jo-1].f,io-1,jo-1),openList)
                    if(cell_values[io-1][jo-1].f>nf||cell_values[io-1][jo-1].f==MAX_VAL) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io-1,jo-1));

                        cell_values[io-1][jo-1].f=nf;
                        cell_values[io-1][jo-1].g=ng;
                        cell_values[io-1][jo-1].h=nh;
                        cell_values[io-1][jo-1].par_row=io;
                        cell_values[io-1][jo-1].par_col=jo;
                    }
                }

            }
            //BOTTOM_RIGHT DIAGONAL
            if(isValid(io+1,jo+1)) {
                number_of_ext++;

                if(destReached(io+1,jo+1,dest)) {
                    cell_values[io+1][jo+1].par_row=io;
                    cell_values[io+1][jo+1].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost= pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io+1][jo+1]==false && isEmptyCell(io+1,jo+1,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io+1,jo+1,dest);
                    nf=ng+nh;

                    // notInOpenList(new openListCell(cell_values[io+1][jo+1].f,io+1,jo+1),openList)
                    if(cell_values[io+1][jo+1].f>nf||cell_values[io+1][jo+1].f==MAX_VAL) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io+1,jo+1));

                        cell_values[io+1][jo+1].f=nf;
                        cell_values[io+1][jo+1].g=ng;
                        cell_values[io+1][jo+1].h=nh;
                        cell_values[io+1][jo+1].par_row=io;
                        cell_values[io+1][jo+1].par_col=jo;
                    }
                }

            }

            //BOTTOM_LEFT DIAGONAL
            if(isValid(io+1,jo-1)) {
                number_of_ext++;

                if(destReached(io+1,jo-1,dest)) {
                    cell_values[io+1][jo-1].par_row=io;
                    cell_values[io+1][jo-1].par_col=jo;
                    System.out.println("Reached the destination "+"("+dest.row+","+dest.col+")");
                    total_cost= pathFinder(cell_values,dest,destNum);
                    reachedDest=true;
                    break;
                }
                //if the neighboor isn't in the blocked list(already vidited) the skip it else do the following
                else if(closedList[io+1][jo-1]==false && isEmptyCell(io+1,jo-1,grid)) {
                    //new g will be increased by 1 cause each move costs 1
                    ng=cell_values[io][jo].g+1;
                    //calculate the new H value of the cell
                    nh=computeH(io+1,jo-1,dest);
                    nf=ng+nh;

                    //notInOpenList(new openListCell(cell_values[io+1][jo-1].f,io+1,jo-1),openList)
                    if(cell_values[io+1][jo-1].f>nf||cell_values[io+1][jo-1].f==MAX_VAL ) {
                        //maybe we have to replace it
                        openList.add(new openListCell(nf,io+1,jo-1));

                        cell_values[io+1][jo-1].f=nf;
                        cell_values[io+1][jo-1].g=ng;
                        cell_values[io+1][jo-1].h=nh;
                        cell_values[io+1][jo-1].par_row=io;
                        cell_values[io+1][jo-1].par_col=jo;
                    }
                }

            }


        }
        //if the following is true we didnt reached the target cell
        if(reachedDest==false) {
            System.out.println("COULDN'T REACH THE DESTINATION"+"("+dest.row+","+dest.col+")");
            return -100;
        }
        System.out.println( "extensions="+number_of_ext+" for destination"+"("+dest.row+","+dest.col+")");
        return total_cost;

    }


    int pathFinder(CellValues [][] cell_values,Cell dest,int destNum) {
        Stack<Cell> path=new Stack<Cell>();
        int row=dest.row;int col=dest.col;
        int total_cost=0;
        //only the first cell if the parent of it's own
        while(!(cell_values[row][col].par_row==row && cell_values[row][col].par_col==col)) {
            //each move costs 1
            total_cost++;
            path.push(new Cell(row,col));
            int tempr=cell_values[row][col].par_row;
            int tempc=cell_values[row][col].par_col;
            row=tempr;
            col=tempc;
        }
        //push and the last cell in the path
        path.push(new Cell(row,col));

        if(destNum==1){
            path1=(Stack<Cell>) path.clone();
        }
        else if(destNum==2){
            path2=(Stack<Cell>)path.clone();
        }


        System.out.println("Total cost:"+total_cost+" for destination"+"("+dest.row+","+dest.col+")");
        return total_cost;


    }


    /*
    @destReached(int row,int col,Cell dest)
    --returns true if we reached the destination cell
    */
    boolean destReached(int row,int col,Cell dest) {
        if(row==dest.row && col==dest.col) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        A_star a=new A_star();
        //row and col for start
        int sR=0,sC=0;
        //row and col for dest1
        int d1R=0,d1C=0;
        //row and col for dest1
        int d2R=0,d2C=0;
        //booleans for input checking
        boolean valid=false,blocked=false;
        //cost for each path
        int total_cost1,total_cost2;
        Scanner scan=new Scanner(System.in);
        Cell start,dest1,dest2;

        a.initializeGrid(a.grid);
        a.printGrid(a.grid);

        //take user input for start ,dest1,dest2
        while(!valid || !blocked) {
            System.out.println("Give starting cell position");
            sR=scan.nextInt();
            sC=scan.nextInt();

            valid=a.isValid(sR,sC);

            if(!valid) {
                System.out.println("Invalid position"+'\n'+"Try again");
            }
            else {
                blocked=a.isEmptyCell(sR,sC,a.grid);
                if(!blocked) {
                    System.out.println("Blocked cell "+'\n'+ "Try again");
                }
            }

        }
        //create starting cell
        start=new Cell(sR,sC);

        valid=false;blocked=false;
        while(!valid || !blocked) {
            System.out.println("Give destination 1 cell position");
            d1R=scan.nextInt();
            d1C=scan.nextInt();

            valid=a.isValid(d1R,d1C);

            if(!valid) {
                System.out.println("Invalid position"+'\n'+"Try again");
            }
            else {
                blocked=a.isEmptyCell(d1R,d1C,a.grid);
                if(!blocked) {
                    System.out.println("Blocked cell "+'\n'+ "Try again");
                }
            }

        }
        //create dest1 cell
        dest1=new Cell(d1R,d1C);

        valid=false;blocked=false;
        while(!valid || !blocked) {
            System.out.println("Give destination 2 cell position");
            d2R=scan.nextInt();
            d2C=scan.nextInt();

            valid=a.isValid(d2R,d2C);

            if(!valid) {
                System.out.println("Invalid position"+'\n'+"Try again");
            }
            else {
                blocked=a.isEmptyCell(d2R,d2C,a.grid);
                if(!blocked) {
                    System.out.println("Blocked cell "+'\n'+ "Try again");
                }
            }

        }
        //create dest2 cell
        dest2=new Cell(d2R,d2C);

        valid=false;blocked=false;

        //call the a* function once per destination
        System.out.println();
        total_cost1=a.a_star(a.grid,start,dest1,1);
        System.out.println();
        total_cost2=a.a_star(a.grid,start,dest2,2);
        System.out.println();


        //if the cost of the 1st path is greater than the 2nd one print the 2nd
        //!=-100 catch the case that no destination was found
        if(total_cost1>total_cost2 ){
            //if(total_cost2!=-100){
                System.out.println("Printing Path for destination:"+"("+dest2.row+","+dest2.col+")");
                a.printPath(a.path2,dest2);
            //}

        }
        else if(total_cost1<total_cost2 ){
            //if(total_cost1!=-100){
                System.out.println("Printing Path for destination:"+"("+dest1.row+","+dest1.col+")");
                a.printPath(a.path1,dest1);
            //}

        }




    }
     /*
    @printPath(Stack<Cell>path,Cell dest)
    --prints the shortest path
    */

    public void printPath(Stack<Cell>path,Cell dest){
        while(!path.isEmpty()) {
            Cell cell=path.pop();
            System.out.println("("+cell.row+","+cell.col+")");
        }
    }

    public void UCS(int [][] grid,Cell start,Cell dest) {
        boolean [][] closedList=new boolean[ARRAY_SIZE][ARRAY_SIZE];
        PriorityQueue<UCScell>	openList=new PriorityQueue<UCScell>() {
            public int compare(UCScell c1,UCScell c2) {
                if(c1.g<c2.g) {
                    return -1;
                }
                else if(c1.g>c2.g) {
                    return 1;
                }
                return 0;
            }
        };

        UCScell source=new UCScell(0,start.row,start.col);
        openList.add(source);
        while(!openList.isEmpty()) {
            UCScell pCell=openList.poll();

        }


    }

}
