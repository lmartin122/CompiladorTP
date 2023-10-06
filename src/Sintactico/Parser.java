//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "grammer.y"
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
import java.util.ArrayList;
import java.io.File;
//#line 24 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short CLASS=257;
public final static short INTERFACE=258;
public final static short IMPLEMENT=259;
public final static short RETURN=260;
public final static short IF=261;
public final static short ELSE=262;
public final static short END_IF=263;
public final static short FOR=264;
public final static short IN=265;
public final static short RANGE=266;
public final static short IMPL=267;
public final static short PRINT=268;
public final static short TOD=269;
public final static short EQUAL_OPERATOR=270;
public final static short NOT_EQUAL_OPERATOR=271;
public final static short GREATER_THAN_OR_EQUAL_OPERATOR=272;
public final static short LESS_THAN_OR_EQUAL_OPERATOR=273;
public final static short MINUS_ASSIGN=274;
public final static short VOID=275;
public final static short LONG=276;
public final static short UINT=277;
public final static short DOUBLE=278;
public final static short CADENA=279;
public final static short ID=280;
public final static short CTE_DOUBLE=281;
public final static short CTE_UINT=282;
public final static short CTE_LONG=283;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    2,    3,    3,    6,    8,
    8,    9,   10,   10,   11,   11,   14,   14,   15,   15,
   16,   17,   12,   19,   21,   22,   22,   20,   20,   23,
   25,    7,   27,   27,    4,   29,   29,   30,   30,   31,
    5,    5,   33,   34,   34,   35,   36,   37,   38,   39,
   26,   42,   42,   42,   43,   43,   43,   43,   43,   18,
   44,   44,   44,   45,   45,   45,   45,   45,   46,   46,
   47,   47,   48,   48,   48,   41,   40,   40,   49,   49,
   13,   13,   50,   32,   51,   51,   52,   52,   53,   28,
   24,   24,   24,   24,   55,   55,   54,   54,   56,   56,
   57,   57,   58,   58,   58,   58,   58,   58,   59,   67,
   60,   60,   60,   60,   60,   60,   68,   68,   68,   65,
   69,   69,   66,   61,   62,   63,   70,   71,   73,   72,
   64,   64,
};
final static short yylen[] = {                            2,
    1,    1,    2,    1,    1,    1,    3,    4,    3,    1,
    2,    1,    1,    1,    3,    2,    1,    3,    1,    3,
    1,    1,    2,    2,    1,    4,    3,    1,    1,    2,
    1,    2,    1,    3,    3,    3,    3,    1,    2,    2,
    5,    4,    3,    1,    2,    1,    2,    1,    3,    1,
    1,    1,    3,    3,    1,    3,    3,    3,    3,    1,
    1,    3,    3,    1,    4,    3,    3,    3,    1,    1,
    1,    3,    1,    1,    1,    1,    1,    1,    5,    4,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    5,    3,    4,    2,    3,    2,    1,    2,    1,    2,
    1,    1,    1,    1,    1,    1,    1,    1,    2,    2,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    7,    9,   11,    1,    1,    1,    1,
    3,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    2,    4,    5,    6,    0,
    0,    0,    3,    0,    0,    7,    0,    0,   35,   90,
   84,    0,    0,   33,    0,   25,   88,   87,   89,    0,
   10,   12,   13,   14,    0,    0,    0,   82,   81,   83,
   85,   86,    8,    0,    0,    0,   38,    0,    0,   16,
    9,   11,   21,    0,   17,    0,    0,   29,   23,   28,
    0,   24,   37,   40,   36,   39,    0,   41,   34,   15,
    0,    0,    0,    0,    0,    0,    0,   94,  123,  115,
    0,  117,  121,    0,   50,  122,    0,   97,  101,  102,
  112,  113,  114,  116,  119,  118,    0,  111,  120,    0,
    0,    0,   44,   46,   18,    0,   76,   73,   74,   75,
    0,   20,   22,   70,    0,    0,   64,   69,   71,    0,
    0,  127,    0,    0,    0,    0,   78,   77,    0,    0,
   92,   98,  109,   27,    0,    0,   48,   47,   43,   45,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   93,    0,    0,  131,    0,    0,   31,   49,    0,   30,
   26,    0,   72,    0,    0,    0,    0,    0,    0,    0,
    0,   66,   67,   68,    0,    0,   80,    0,   91,   65,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   79,
    0,   96,    0,   99,  103,  104,  105,  106,  107,  108,
    0,    0,  128,    0,   95,  100,    0,  124,  130,    0,
    0,  129,    0,  125,    0,    0,  126,
};
final static short yydgoto[] = {                          4,
    5,    6,    7,    8,    9,   16,   17,   30,   31,   32,
   33,   34,   35,   54,   55,   56,  112,  113,   36,   59,
   37,   62,  136,   60,  156,  142,   23,   21,   19,   46,
   47,   38,   68,  102,  103,  104,  138,   83,   84,  129,
  114,  143,  144,  145,  116,  117,  118,  119,   86,   39,
   40,   41,   42,   87,  188,  193,   88,  194,   89,   90,
   91,   92,   93,   94,   95,   96,   97,   98,   99,  123,
  204,  210,  213,
};
final static short yysindex[] = {                      -208,
 -244, -222, -179,    0, -208,    0,    0,    0,    0, -123,
  -24, -188,    0, -188, -199,    0,  -16, -111,    0,    0,
    0,   37,   52,    0,   69,    0,    0,    0,    0, -117,
    0,    0,    0,    0, -154,  -27, -151,    0,    0,    0,
    0,    0,    0,   88, -151, -110,    0,   10, -188,    0,
    0,    0,    0,  -21,    0,   74,  232,    0,    0,    0,
   97,    0,    0,    0,    0,    0, -137,    0,    0,    0,
 -154,  -34,   96,  101, -138, -136,  104,    0,    0,    0,
 -154,    0,    0,  -56,    0,    0,  356,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  102,    0,    0,   57,
  -27, -109,    0,    0,    0,  105,    0,    0,    0,    0,
  -34,    0,    0,    0,   30,  -12,    0,    0,    0,   23,
  -34,    0, -114,  109,  -38,  108,    0,    0,  -34,  111,
    0,    0,    0,    0, -154,  127,    0,    0,    0,    0,
  -30,  129, -238,  -53,   30,  -34,  -34,  -30,  -30,  -30,
    0,  131,  -92,    0,  132,  136,    0,    0,   53,    0,
    0,  138,    0,  -34,  -34,  -34,  -34,  -34,  -34,  -12,
  -12,    0,    0,    0,   58,  140,    0,  141,    0,    0,
  -53,  -53,   30,   30,   30,   30,  276, -160, -163,    0,
  104,    0,  361,    0,    0,    0,    0,    0,    0,    0,
   58,  143,    0, -163,    0,    0,  -81,    0,    0, -163,
  144,    0,  148,    0,   58,  146,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,  184,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   71,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    1,    0,    0,
    0,    0,    0,    0,    0,  -20,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  379,  -57,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -15,    2,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  151,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  335,  186,   68,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   24,
   46,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  271,  308,   90,  113,  142,  164,    0,    0,    0,    0,
  -49,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  187,    0,    0,    0,  179,    0,    0,  167,    0,
    0,  -36,  -46,  117,  128,   65,    0,    0,  -54,  100,
    9,  158,    0,  -35,    0,  -28,    0,    4,    0,    0,
  160,  195,    0,    0,  107,    0,    0,    0,    0,    0,
  -31,    0,  -43,  -52,  -23,  -78,    0, -170,    0,    0,
    0,    0,    0,    0, -173,    0,  123,   18,    0,    0,
 -156, -127, -113, -105,  -93,  -83,    0,    0,    0,    0,
    0,    0,    0,
};
final static int YYTABLESIZE=659;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         15,
   42,  111,  155,   76,  128,  111,  168,   51,  169,  111,
   81,   76,  101,   44,   65,  139,   58,   24,  203,  115,
   80,   82,   70,   19,  150,   85,   45,  207,   60,  148,
  195,  164,  165,  209,  149,   10,  195,   71,   19,  212,
   81,  216,   61,   60,   61,   61,   61,  101,    1,    2,
   80,   82,   69,  135,   45,   85,   25,   11,    3,  196,
   61,   61,  162,   61,   62,  196,   62,   62,   62,  172,
  173,  174,  146,  197,  147,   26,   27,   28,   29,  197,
   20,  198,   62,   62,   12,   62,   63,  198,   63,   63,
   63,   20,  152,  199,   48,   57,  157,  134,   18,  199,
  158,  201,  202,  200,   63,   63,   15,   63,   55,  200,
   49,   55,   50,  183,  184,  185,  186,  108,  109,  110,
  181,  182,  170,  171,   61,   53,   61,   55,   61,   55,
   58,   63,   67,   58,   72,   14,  100,   26,   25,  120,
  121,  122,  124,  125,  141,  133,   62,  151,   62,   58,
  153,   58,  154,   59,  159,   85,   59,   26,   27,   28,
   29,   85,   20,   26,   26,   26,   71,  161,   63,  163,
   63,  175,   59,  176,   59,  177,  178,  179,  180,  189,
  187,  211,   56,    1,  190,   56,  208,  214,  215,  217,
   55,   13,   55,   32,  110,   43,   52,  126,  105,  160,
  137,   56,   64,   56,   57,   66,   22,   57,  140,  132,
  206,    0,   58,    0,   58,    0,   76,  127,  166,  167,
    0,    0,   90,   57,   76,   57,   52,    0,    0,   52,
  106,    0,    0,    0,  106,   59,    0,   59,    0,    0,
    0,  107,  108,  109,  110,  107,  108,  109,  110,  107,
  108,  109,  110,    0,    0,    0,    0,   42,   42,    0,
    0,   61,   61,    0,   56,   61,   56,   42,    0,   61,
    0,   61,   61,   61,   61,   79,   61,   61,   61,   61,
    0,   61,    0,   62,   62,    0,   57,   62,   57,    0,
    0,   62,    0,   62,   62,   62,   62,    0,   62,   62,
   62,   62,    0,   62,    0,   63,   63,    0,   52,   63,
   52,   53,    0,   63,   53,   63,   63,   63,   63,   79,
   63,   63,   63,   63,    0,   63,    0,   55,   55,    0,
    0,   55,   27,   28,   29,   55,   20,   55,   55,   55,
   55,    0,   55,   55,   55,   55,    0,   55,   54,   58,
   58,   54,    0,   58,   57,    0,   78,   58,    0,   58,
   58,   58,   58,    0,   58,   58,   58,   58,    0,   58,
    0,    0,   59,   59,    0,   51,   59,    0,   51,    0,
   59,    0,   59,   59,   59,   59,    0,   59,   59,   59,
   59,    0,   59,   53,    0,   53,    0,    0,    0,   79,
  192,   56,   56,    0,   79,   56,    0,    0,    0,   56,
    0,   56,   56,   56,   56,    0,   56,   56,   56,   56,
    0,   56,  132,   57,   57,    0,    0,   57,    0,    0,
   54,   57,   54,   57,   57,   57,   57,    0,   57,   57,
   57,   57,    0,   57,    0,   52,   52,    0,    0,   52,
    0,    0,    0,   52,    0,   52,   52,   51,    0,   51,
   52,   52,   52,   52,    0,   52,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   57,    0,
  131,    0,    0,    0,    0,  205,    0,    0,    0,    0,
    0,   73,   74,    0,    0,   75,    0,    0,    0,   76,
    0,  132,    0,  132,    0,    0,   26,   27,   28,   29,
    0,   77,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   53,   53,    0,    0,   53,    0,   74,    0,   53,   75,
   53,   53,    0,   76,    0,   53,   53,   53,   53,    0,
   53,    0,    0,    0,    0,  191,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   54,   54,    0,
    0,   54,    0,    0,    0,   54,    0,   54,   54,    0,
    0,    0,   54,   54,   54,   54,    0,   54,    0,    0,
    0,    0,    0,    0,   51,   51,    0,    0,   51,    0,
    0,    0,   51,    0,    0,    0,    0,    0,    0,   51,
   51,   51,   51,    0,   51,  130,   74,    0,    0,   75,
    0,   74,    0,   76,   75,    0,    0,    0,   76,    0,
   26,   27,   28,   29,    0,   77,    0,    0,  132,  132,
  191,    0,  132,    0,    0,    0,  132,    0,    0,    0,
    0,    0,    0,  132,  132,  132,  132,    0,  132,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                        123,
    0,   40,   41,   61,   61,   40,   60,  125,   62,   40,
   57,   61,   67,  125,  125,  125,   44,   14,  189,   72,
   57,   57,   44,   44,   37,   57,   18,  201,   44,   42,
  187,  270,  271,  204,   47,  280,  193,   59,   59,  210,
   87,  215,   41,   59,   43,   44,   45,  102,  257,  258,
   87,   87,   49,  100,   46,   87,  256,  280,  267,  187,
   59,   60,  141,   62,   41,  193,   43,   44,   45,  148,
  149,  150,   43,  187,   45,  275,  276,  277,  278,  193,
  280,  187,   59,   60,  264,   62,   41,  193,   43,   44,
   45,  280,  121,  187,   58,  123,  125,   41,  123,  193,
  129,  262,  263,  187,   59,   60,  123,   62,   41,  193,
   59,   44,   44,  166,  167,  168,  169,  281,  282,  283,
  164,  165,  146,  147,  123,  280,  125,   60,  280,   62,
   41,   44,  123,   44,   61,  259,   40,  275,  256,   44,
   40,  280,  279,   40,   40,   44,  123,  125,  125,   60,
  265,   62,   44,   41,   44,  187,   44,  275,  276,  277,
  278,  193,  280,  275,  275,  275,   59,   41,  123,   41,
  125,   41,   60,  266,   62,   44,   41,  125,   41,   40,
  123,  263,   41,    0,   44,   44,   44,   44,   41,   44,
  123,    5,  125,  123,   44,   17,   30,   81,   71,  135,
  101,   60,   45,   62,   41,   46,   12,   44,  102,   87,
  193,   -1,  123,   -1,  125,   -1,  274,  274,  272,  273,
   -1,   -1,  280,   60,  274,   62,   41,   -1,   -1,   44,
  269,   -1,   -1,   -1,  269,  123,   -1,  125,   -1,   -1,
   -1,  280,  281,  282,  283,  280,  281,  282,  283,  280,
  281,  282,  283,   -1,   -1,   -1,   -1,  257,  258,   -1,
   -1,  260,  261,   -1,  123,  264,  125,  267,   -1,  268,
   -1,  270,  271,  272,  273,   44,  275,  276,  277,  278,
   -1,  280,   -1,  260,  261,   -1,  123,  264,  125,   -1,
   -1,  268,   -1,  270,  271,  272,  273,   -1,  275,  276,
  277,  278,   -1,  280,   -1,  260,  261,   -1,  123,  264,
  125,   41,   -1,  268,   44,  270,  271,  272,  273,   44,
  275,  276,  277,  278,   -1,  280,   -1,  260,  261,   -1,
   -1,  264,  276,  277,  278,  268,  280,  270,  271,  272,
  273,   -1,  275,  276,  277,  278,   -1,  280,   41,  260,
  261,   44,   -1,  264,  123,   -1,  125,  268,   -1,  270,
  271,  272,  273,   -1,  275,  276,  277,  278,   -1,  280,
   -1,   -1,  260,  261,   -1,   41,  264,   -1,   44,   -1,
  268,   -1,  270,  271,  272,  273,   -1,  275,  276,  277,
  278,   -1,  280,  123,   -1,  125,   -1,   -1,   -1,   44,
  125,  260,  261,   -1,   44,  264,   -1,   -1,   -1,  268,
   -1,  270,  271,  272,  273,   -1,  275,  276,  277,  278,
   -1,  280,   44,  260,  261,   -1,   -1,  264,   -1,   -1,
  123,  268,  125,  270,  271,  272,  273,   -1,  275,  276,
  277,  278,   -1,  280,   -1,  260,  261,   -1,   -1,  264,
   -1,   -1,   -1,  268,   -1,  270,  271,  123,   -1,  125,
  275,  276,  277,  278,   -1,  280,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  123,   -1,
  125,   -1,   -1,   -1,   -1,  125,   -1,   -1,   -1,   -1,
   -1,  260,  261,   -1,   -1,  264,   -1,   -1,   -1,  268,
   -1,  123,   -1,  125,   -1,   -1,  275,  276,  277,  278,
   -1,  280,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  260,  261,   -1,   -1,  264,   -1,  261,   -1,  268,  264,
  270,  271,   -1,  268,   -1,  275,  276,  277,  278,   -1,
  280,   -1,   -1,   -1,   -1,  280,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  260,  261,   -1,
   -1,  264,   -1,   -1,   -1,  268,   -1,  270,  271,   -1,
   -1,   -1,  275,  276,  277,  278,   -1,  280,   -1,   -1,
   -1,   -1,   -1,   -1,  260,  261,   -1,   -1,  264,   -1,
   -1,   -1,  268,   -1,   -1,   -1,   -1,   -1,   -1,  275,
  276,  277,  278,   -1,  280,  260,  261,   -1,   -1,  264,
   -1,  261,   -1,  268,  264,   -1,   -1,   -1,  268,   -1,
  275,  276,  277,  278,   -1,  280,   -1,   -1,  260,  261,
  280,   -1,  264,   -1,   -1,   -1,  268,   -1,   -1,   -1,
   -1,   -1,   -1,  275,  276,  277,  278,   -1,  280,
};
}
final static short YYFINAL=4;
final static short YYMAXTOKEN=283;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"CLASS","INTERFACE","IMPLEMENT",
"RETURN","IF","ELSE","END_IF","FOR","IN","RANGE","IMPL","PRINT","TOD",
"EQUAL_OPERATOR","NOT_EQUAL_OPERATOR","GREATER_THAN_OR_EQUAL_OPERATOR",
"LESS_THAN_OR_EQUAL_OPERATOR","MINUS_ASSIGN","VOID","LONG","UINT","DOUBLE",
"CADENA","ID","CTE_DOUBLE","CTE_UINT","CTE_LONG",
};
final static String yyrule[] = {
"$accept : program",
"program : type_declarations",
"type_declarations : type_declaration",
"type_declarations : type_declarations type_declaration",
"type_declaration : class_declaration",
"type_declaration : interface_declaration",
"type_declaration : implement_for_declaration",
"class_declaration : CLASS ID class_body",
"class_declaration : CLASS ID interfaces class_body",
"class_body : '{' class_body_declarations '}'",
"class_body_declarations : class_body_declaration",
"class_body_declarations : class_body_declarations class_body_declaration",
"class_body_declaration : class_member_declaration",
"class_member_declaration : field_declaration",
"class_member_declaration : method_declaration",
"field_declaration : type variable_declarators ','",
"field_declaration : error ','",
"variable_declarators : variable_declarator",
"variable_declarators : variable_declarators ';' variable_declarator",
"variable_declarator : variable_declarator_id",
"variable_declarator : variable_declarator_id '=' variable_initializer",
"variable_declarator_id : ID",
"variable_initializer : arithmetic_operation",
"method_declaration : method_header method_body",
"method_header : result_type method_declarator",
"result_type : VOID",
"method_declarator : ID '(' formal_parameter ')'",
"method_declarator : ID '(' ')'",
"method_body : block",
"method_body : ','",
"formal_parameter : type variable_declarator_id",
"real_parameter : expression",
"interfaces : IMPLEMENT interface_type_list",
"interface_type_list : type_name",
"interface_type_list : interface_type_list ';' type_name",
"interface_declaration : INTERFACE ID interface_body",
"interface_body : '{' interface_member_declaration '}'",
"interface_body : '{' '}' ','",
"interface_member_declaration : interface_method_declaration",
"interface_member_declaration : interface_member_declaration interface_method_declaration",
"interface_method_declaration : result_type method_declarator",
"implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
"implement_for_declaration : IMPL FOR reference_type ':'",
"implement_for_body : '{' implement_for_body_declarations '}'",
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : method_header implement_for_method_body",
"implement_for_method_body : method_body",
"assignment : left_hand_side assignment_operator expression",
"left_hand_side : expression_name",
"expression : equality_expression",
"equality_expression : relational_expression",
"equality_expression : equality_expression EQUAL_OPERATOR relational_expression",
"equality_expression : equality_expression NOT_EQUAL_OPERATOR relational_expression",
"relational_expression : additive_expression",
"relational_expression : relational_expression '<' additive_expression",
"relational_expression : relational_expression '>' additive_expression",
"relational_expression : relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression",
"relational_expression : relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression",
"arithmetic_operation : additive_expression",
"additive_expression : multiplicative_expression",
"additive_expression : additive_expression '+' multiplicative_expression",
"additive_expression : additive_expression '-' multiplicative_expression",
"multiplicative_expression : unary_expression",
"multiplicative_expression : TOD '(' unary_expression ')'",
"multiplicative_expression : multiplicative_expression '*' unary_expression",
"multiplicative_expression : multiplicative_expression '/' unary_expression",
"multiplicative_expression : multiplicative_expression '%' unary_expression",
"unary_expression : term",
"unary_expression : expression_name",
"term : factor",
"term : '(' expression ')'",
"factor : CTE_DOUBLE",
"factor : CTE_UINT",
"factor : CTE_LONG",
"expression_name : ID",
"assignment_operator : '='",
"assignment_operator : MINUS_ASSIGN",
"method_invocation : ID '(' real_parameter ')' ','",
"method_invocation : ID '(' ')' ','",
"type : primitive_type",
"type : reference_type",
"primitive_type : numeric_type",
"reference_type : type_name",
"numeric_type : integral_type",
"numeric_type : floating_type",
"integral_type : UINT",
"integral_type : LONG",
"floating_type : DOUBLE",
"type_name : ID",
"block : '{' block_statements RETURN ',' '}'",
"block : '{' block_statements '}'",
"block : '{' RETURN ',' '}'",
"block : '{' '}'",
"executables_block : '{' executables_block_statements '}'",
"executables_block : '{' '}'",
"block_statements : block_statement",
"block_statements : block_statements block_statement",
"executables_block_statements : executables_block_statament",
"executables_block_statements : executables_block_statements executables_block_statament",
"block_statement : local_variable_declaration_statement",
"block_statement : statement",
"executables_block_statament : if_then_statement",
"executables_block_statament : if_then_else_statement",
"executables_block_statament : for_in_range_statement",
"executables_block_statament : print_statement",
"executables_block_statament : expression_statement",
"executables_block_statament : empty_statement",
"local_variable_declaration_statement : local_variable_declaration ','",
"local_variable_declaration : type variable_declarators",
"statement : statement_without_trailing_substatement",
"statement : if_then_statement",
"statement : if_then_else_statement",
"statement : for_in_range_statement",
"statement : method_declaration",
"statement : print_statement",
"statement_without_trailing_substatement : block",
"statement_without_trailing_substatement : empty_statement",
"statement_without_trailing_substatement : expression_statement",
"expression_statement : statement_expression",
"statement_expression : assignment",
"statement_expression : method_invocation",
"empty_statement : ','",
"if_then_statement : IF '(' expression ')' executables_block END_IF ','",
"if_then_else_statement : IF '(' expression ')' executables_block ELSE executables_block END_IF ','",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' executables_block ','",
"for_variable : ID",
"for_init : factor",
"for_update : factor",
"for_end : factor",
"print_statement : PRINT CADENA ','",
"print_statement : PRINT",
};

//#line 350 "grammer.y"

private static AnalizadorLexico aLexico;

// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
    int token = -1;
    while (!aLexico.hasFinishedTokenizer()) {
        token = aLexico.generateToken();
        if(token >= 0 ){ // deberia devolver cuando llega a un estado final
            // yyval = new ParserVal(token); //genera la referencia a la tabla de simbolos?
            return token;
        }

    }
    
    return token;
}


// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println(msg);
}


  private static ArrayList<String> listFilesInDirectory(String path) {
    // Obtén el directorio actual
    File element = new File(System.getProperty("user.dir") + "/" + path);
    ArrayList<String> out = new ArrayList<>();

    // Verifica si es un directorio o archivo válido
    if (element.isDirectory() || element.isFile()) {
      // Lista de archivos y directorios en el directorio actual
      File[] filesAndDirs = element.listFiles();

      // Itera a través de los archivos y directorios
      int i = 0;
      for (File fileOrDir : filesAndDirs) {
        String name = fileOrDir.getName();
        System.out.println("[" + i + "]" + ": " + name);
        out.add(name);
        i++;
      }
    } else {
      System.err.println("No es un directorio válido.");
    }

    return out;
  }

  private static String generatePath() {
    ArrayList<String> directories = listFilesInDirectory("sample_programs");
    String path = "";

    if (!directories.isEmpty()) {
      Scanner scanner = new Scanner(System.in);
      int indice = -1;

      while (indice < 0) {
        System.out.print("Ingrese el numero de carpeta a acceder: ");
        String input = scanner.nextLine();
        indice = Integer.parseInt(input);
        if (indice < directories.size() || indice >= 0) {
          path = directories.get(indice);
          directories = listFilesInDirectory("sample_programs" + "/" + path);
        } else {
          System.out.println("El indice no es correcto, ingrese nuevamente...");
          indice = -1;
        }

      }

      if (!directories.isEmpty()) {
        indice = -1;

        while (indice < 0) {

          System.out.print("Ingrese el numero de archivo binario a compilar: ");
          String input = scanner.nextLine();
          indice = Integer.parseInt(input);

          if (indice < directories.size() || indice >= 0) {
            path += "/" + directories.get(Integer.parseInt(input));
          } else {

            System.out.println("El indice no es correcto, ingrese nuevamente...");
            indice = -1;
          }

        }
      }
      scanner.close();
    }
    return path;
  }


public static void main (String [] args){
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }

    Parser aSintactico = new Parser();
    aSintactico.run();


    Logger.logError(1, "Este es un error.");
    Logger.logWarning(2, "Esta es una advertencia.");

    Logger.dumpLog();
    System.out.println(aLexico.getProgram());
}


//#line 673 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 42:
//#line 130 "grammer.y"
{Logger.logError(0, "Es necesario implementar el cuerpo del metodo.");}
break;
case 92:
//#line 260 "grammer.y"
{Logger.logError(0, "Es necesario declarar el retorno del bloque.");}
break;
case 94:
//#line 262 "grammer.y"
{Logger.logError(0, "Es necesario declarar el retorno del bloque.");}
break;
case 132:
//#line 345 "grammer.y"
{Logger.logError(0, "Se esperaba una cadena.");}
break;
//#line 838 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
