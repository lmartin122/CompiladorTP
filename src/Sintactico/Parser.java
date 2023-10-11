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
import Tools.Tupla;
import Tools.TablaSimbolos;
import java.io.IOException;
//#line 27 "Parser.java"

public class Parser {

  boolean yydebug; // do I want debug output?
  int yynerrs; // number of errors so far
  int yyerrflag; // was there an error?
  int yychar; // the current working character

  // ########## MESSAGES ##########
  // ###############################################################
  // method: debug
  // ###############################################################
  void debug(String msg) {
    if (yydebug)
      System.out.println(msg);
  }

  // ########## STATE STACK ##########
  final static int YYSTACKSIZE = 500; // maximum stack size
  int statestk[] = new int[YYSTACKSIZE]; // state stack
  int stateptr;
  int stateptrmax; // highest index of stackptr
  int statemax; // state when highest index reached
  // ###############################################################
  // methods: state stack push,pop,drop,peek
  // ###############################################################

  final void state_push(int state) {
    try {
      stateptr++;
      statestk[stateptr] = state;
    } catch (ArrayIndexOutOfBoundsException e) {
      int oldsize = statestk.length;
      int newsize = oldsize * 2;
      int[] newstack = new int[newsize];
      System.arraycopy(statestk, 0, newstack, 0, oldsize);
      statestk = newstack;
      statestk[stateptr] = state;
    }
  }

  final int state_pop() {
    return statestk[stateptr--];
  }

  final void state_drop(int cnt) {
    stateptr -= cnt;
  }

  final int state_peek(int relative) {
    return statestk[stateptr - relative];
  }

  // ###############################################################
  // method: init_stacks : allocate and prepare stacks
  // ###############################################################
  final boolean init_stacks() {
    stateptr = -1;
    val_init();
    return true;
  }

  // ###############################################################
  // method: dump_stacks : show n levels of the stacks
  // ###############################################################
  void dump_stacks(int count) {
    int i;
    System.out.println("=index==state====value=     s:" + stateptr + "  v:" + valptr);
    for (i = 0; i < count; i++)
      System.out.println(" " + i + "    " + statestk[i] + "      " + valstk[i]);
    System.out.println("======================");
  }

  // ########## SEMANTIC VALUES ##########
  // public class ParserVal is defined in ParserVal.java

  String yytext;// user variable to return contextual strings
  ParserVal yyval; // used to return semantic vals from action routines
  ParserVal yylval;// the 'lval' (result) I got from yylex()
  ParserVal valstk[];
  int valptr;

  // ###############################################################
  // methods: value stack push,pop,drop,peek.
  // ###############################################################
  void val_init() {
    valstk = new ParserVal[YYSTACKSIZE];
    yyval = new ParserVal();
    yylval = new ParserVal();
    valptr = -1;
  }

  void val_push(ParserVal val) {
    if (valptr >= YYSTACKSIZE)
      return;
    valstk[++valptr] = val;
  }

  ParserVal val_pop() {
    if (valptr < 0)
      return new ParserVal();
    return valstk[valptr--];
  }

  void val_drop(int cnt) {
    int ptr;
    ptr = valptr - cnt;
    if (ptr < 0)
      return;
    valptr = ptr;
  }

  ParserVal val_peek(int relative) {
    int ptr;
    ptr = valptr - relative;
    if (ptr < 0)
      return new ParserVal();
    return valstk[ptr];
  }

  final ParserVal dup_yyval(ParserVal val) {
    ParserVal dup = new ParserVal();
    dup.ival = val.ival;
    dup.dval = val.dval;
    dup.sval = val.sval;
    dup.obj = val.obj;
    return dup;
  }

  // #### end semantic value section ####
  public final static short CLASS = 257;
  public final static short INTERFACE = 258;
  public final static short IMPLEMENT = 259;
  public final static short RETURN = 260;
  public final static short IF = 261;
  public final static short ELSE = 262;
  public final static short END_IF = 263;
  public final static short FOR = 264;
  public final static short IN = 265;
  public final static short RANGE = 266;
  public final static short IMPL = 267;
  public final static short PRINT = 268;
  public final static short TOD = 269;
  public final static short EQUAL_OPERATOR = 270;
  public final static short NOT_EQUAL_OPERATOR = 271;
  public final static short GREATER_THAN_OR_EQUAL_OPERATOR = 272;
  public final static short LESS_THAN_OR_EQUAL_OPERATOR = 273;
  public final static short MINUS_ASSIGN = 274;
  public final static short VOID = 275;
  public final static short LONG = 276;
  public final static short UINT = 277;
  public final static short DOUBLE = 278;
  public final static short CADENA = 279;
  public final static short ID = 280;
  public final static short CTE_DOUBLE = 281;
  public final static short CTE_UINT = 282;
  public final static short CTE_LONG = 283;
  public final static short YYERRCODE = 256;
  final static short yylhs[] = { -1,
      0, 1, 1, 2, 2, 2, 2, 3, 3, 3,
      7, 9, 9, 10, 11, 11, 12, 15, 15, 16,
      16, 17, 18, 13, 20, 22, 23, 23, 23, 23,
      23, 21, 21, 24, 26, 8, 27, 27, 4, 4,
      29, 29, 29, 29, 30, 30, 31, 31, 5, 5,
      5, 5, 33, 34, 34, 35, 36, 37, 38, 39,
      41, 42, 42, 42, 43, 43, 43, 43, 43, 19,
      44, 44, 44, 45, 45, 45, 45, 45, 46, 46,
      47, 47, 47, 48, 48, 48, 48, 48, 48, 40,
      40, 49, 49, 49, 14, 14, 50, 32, 51, 51,
      52, 52, 53, 28, 25, 25, 25, 25, 55, 55,
      54, 54, 56, 56, 6, 6, 57, 57, 57, 57,
      57, 57, 58, 66, 59, 59, 59, 59, 59, 59,
      67, 67, 67, 64, 68, 68, 65, 60, 60, 60,
      60, 60, 60, 60, 61, 61, 61, 61, 61, 61,
      61, 61, 61, 62, 62, 62, 62, 62, 69, 70,
      72, 71, 63, 63, 63,
  };
  final static short yylen[] = { 2,
      1, 1, 2, 1, 1, 1, 1, 3, 4, 3,
      3, 1, 2, 1, 1, 1, 3, 1, 3, 1,
      3, 1, 1, 2, 2, 1, 4, 4, 6, 3,
      3, 1, 1, 2, 1, 2, 1, 3, 3, 4,
      3, 3, 2, 2, 1, 2, 2, 3, 5, 6,
      6, 4, 3, 1, 2, 1, 2, 1, 3, 1,
      1, 1, 3, 3, 1, 3, 3, 3, 3, 1,
      1, 3, 3, 1, 4, 3, 3, 3, 1, 1,
      1, 3, 2, 1, 1, 1, 2, 2, 2, 1,
      1, 5, 4, 7, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 5, 3, 4, 2, 3, 2,
      1, 2, 1, 2, 1, 1, 1, 1, 1, 1,
      1, 1, 2, 2, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 7, 7, 6,
      6, 7, 7, 7, 9, 9, 9, 9, 9, 9,
      9, 9, 9, 12, 12, 13, 6, 8, 1, 1,
      1, 1, 3, 3, 3,
  };
  final static short yydefred[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 26, 102, 101,
      103, 0, 0, 137, 0, 0, 2, 4, 5, 6,
      7, 129, 0, 0, 0, 131, 0, 135, 0, 136,
      95, 97, 99, 100, 115, 116, 126, 127, 128, 130,
      133, 132, 0, 125, 134, 0, 0, 0, 0, 0,
      0, 98, 159, 0, 0, 0, 0, 0, 0, 0,
      108, 111, 0, 3, 22, 0, 18, 0, 33, 24,
      32, 0, 25, 91, 90, 0, 123, 165, 0, 10,
      0, 0, 8, 0, 0, 0, 0, 39, 0, 0,
      80, 84, 85, 86, 0, 0, 0, 0, 0, 0,
      0, 74, 79, 81, 0, 0, 0, 164, 163, 0,
      35, 0, 0, 0, 0, 106, 112, 0, 0, 0,
      0, 59, 0, 12, 14, 15, 16, 0, 96, 0,
      52, 104, 0, 37, 9, 40, 43, 0, 0, 45,
      44, 0, 0, 0, 87, 89, 88, 83, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 93, 0, 0, 107, 0, 19,
      21, 23, 31, 0, 0, 30, 0, 11, 13, 0,
      0, 0, 54, 56, 0, 0, 47, 41, 46, 42,
      0, 60, 0, 0, 117, 118, 119, 120, 121, 122,
      0, 82, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 76, 77, 78, 0, 0, 0, 0,
      49, 0, 92, 105, 34, 28, 0, 27, 17, 58,
      57, 53, 55, 38, 48, 110, 0, 113, 0, 0,
      0, 0, 75, 0, 0, 0, 141, 0, 0, 140,
      157, 0, 160, 0, 51, 50, 0, 0, 109, 114,
      0, 0, 143, 0, 0, 144, 142, 0, 0, 138,
      0, 0, 139, 0, 0, 94, 29, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 158, 162, 0, 150,
      151, 153, 152, 145, 146, 149, 148, 147, 0, 161,
      0, 0, 0, 154, 155, 156,
  };
  final static short yydgoto[] = { 15,
      16, 17, 18, 19, 20, 21, 80, 84, 123, 124,
      125, 126, 22, 23, 66, 67, 68, 171, 111, 24,
      70, 25, 73, 175, 26, 112, 133, 134, 88, 139,
      140, 192, 131, 182, 183, 184, 231, 28, 29, 76,
      97, 98, 99, 100, 101, 102, 103, 104, 30, 31,
      32, 33, 34, 63, 193, 237, 194, 35, 36, 195,
      196, 197, 198, 199, 200, 43, 44, 45, 54, 254,
      289, 301,
  };
  final static short yysindex[] = { 121,
      -242, -233, -225, 61, -216, -173, -227, 0, 0, 0,
      0, 81, 261, 0, 0, 121, 0, 0, 0, 0,
      0, 0, -148, -16, -146, 0, 0, 0, -4, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 94, 0, 0, 102, 36, 108, -104, -24,
      -40, 0, 0, -96, -222, 129, 133, -34, -98, 135,
      0, 0, 286, 0, 0, 124, 0, 136, 0, 0,
      0, -18, 0, 0, 0, -30, 0, 0, 200, 0,
      69, -80, 0, 36, 165, -117, -38, 0, 174, 172,
      0, 0, 0, 0, -167, 78, 179, -162, 131, 99,
      171, 0, 0, 0, -60, 187, 217, 0, 0, 236,
      0, -5, 99, 160, 251, 0, 0, -148, -30, -82,
      90, 0, 185, 0, 0, 0, 0, -148, 0, 50,
      0, 0, 276, 0, 0, 0, 0, -221, -111, 0,
      0, -9, -42, 82, 0, 0, 0, 0, 279, 144,
      -30, -30, -30, -30, -30, -30, -30, -30, 82, 82,
      82, -20, 69, -99, 0, 101, 328, 0, 255, 0,
      0, 0, 0, -148, 258, 0, 89, 0, 0, 24,
      -16, -108, 0, 0, -80, 351, 0, 0, 0, 0,
      166, 0, -91, -59, 0, 0, 0, 0, 0, 0,
      365, 0, -230, -35, -32, 131, 131, 99, 99, 99,
      99, 171, 171, 0, 0, 0, 369, 92, 381, 392,
      0, 391, 0, 0, 0, 0, 183, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 167, 0, -42, 399,
      -42, 400, 0, 405, -42, 410, 0, -42, 411, 0,
      0, 417, 0, 420, 0, 0, 415, 429, 0, 0,
      218, 219, 0, 223, 224, 0, 0, 225, 226, 0,
      -215, 227, 0, 146, -27, 0, 0, 447, 448, 449,
      450, 452, 454, 455, 456, 457, 0, 0, 436, 0,
      0, 0, 0, 0, 0, 0, 0, 0, -27, 0,
      461, 177, -19, 0, 0, 0,
  };
  final static short yyrindex[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 75, 0, 0, 0, 497, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 107, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 445, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 460, 0, 45, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 464, -37, 120,
      1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 76, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 384, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 46, 71, 145, 309, 380,
      388, 26, 51, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0,
  };
  final static short yygindex[] = { 0,
      0, 492, 0, 0, 0, 17, -22, 0, 0, 386,
      0, 0, -25, 64, 382, 393, 338, 0, -45, -107,
      332, -14, 377, 397, -11, 0, 0, 334, 0, 433,
      11, 451, 60, 0, 341, 0, 0, 0, 0, 0,
      428, 0, 194, 318, 262, 324, 0, -197, 0, 0,
      0, 0, 0, 0, -100, 0, -85, 0, 0, 40,
      66, 77, 84, 162, 453, 0, 0, 0, 0, 0,
      0, 0,
  };
  final static int YYTABLESIZE = 661;
  static short yytable[];
  static {
    yytable();
  }

  static void yytable() {
    yytable = new short[] { 96,
        71, 14, 141, 62, 95, 96, 110, 137, 247, 96,
        95, 250, 71, 188, 95, 87, 232, 95, 79, 218,
        253, 121, 181, 130, 306, 72, 83, 69, 56, 62,
        122, 190, 244, 106, 186, 167, 46, 47, 166, 37,
        284, 71, 173, 71, 71, 71, 49, 285, 46, 204,
        73, 57, 37, 127, 50, 37, 75, 52, 72, 71,
        71, 135, 71, 52, 205, 38, 72, 229, 72, 72,
        72, 138, 138, 172, 181, 70, 39, 288, 38, 117,
        191, 38, 118, 40, 72, 72, 63, 72, 20, 39,
        55, 73, 39, 73, 73, 73, 40, 127, 86, 40,
        51, 300, 37, 20, 120, 238, 13, 151, 152, 73,
        73, 64, 73, 145, 146, 147, 70, 96, 148, 70,
        58, 96, 95, 71, 138, 71, 95, 138, 38, 228,
        176, 65, 227, 72, 70, 98, 95, 77, 261, 39,
        264, 157, 128, 158, 268, 78, 40, 271, 72, 189,
        72, 260, 189, 262, 82, 265, 220, 8, 79, 269,
        65, 41, 272, 8, 14, 81, 8, 60, 105, 71,
        239, 240, 108, 73, 41, 73, 109, 41, 114, 65,
        46, 65, 118, 174, 174, 68, 128, 14, 287, 14,
        155, 130, 156, 9, 10, 11, 119, 52, 70, 132,
        70, 304, 241, 242, 68, 162, 68, 161, 136, 14,
        14, 144, 159, 59, 143, 89, 305, 160, 4, 150,
        14, 5, 219, 221, 41, 7, 245, 246, 90, 248,
        249, 85, 62, 62, 90, 217, 8, 12, 90, 91,
        92, 93, 94, 13, 163, 91, 92, 93, 94, 91,
        92, 93, 94, 92, 93, 94, 71, 71, 71, 46,
        71, 71, 71, 71, 71, 8, 191, 71, 71, 74,
        71, 71, 71, 71, 164, 71, 71, 71, 71, 165,
        71, 72, 72, 72, 168, 72, 72, 72, 72, 72,
        236, 259, 72, 72, 169, 72, 72, 72, 72, 191,
        72, 72, 72, 72, 14, 72, 73, 73, 73, 178,
        73, 73, 73, 73, 73, 63, 63, 73, 73, 202,
        73, 73, 73, 73, 8, 73, 73, 73, 73, 14,
        73, 70, 70, 70, 185, 70, 70, 70, 70, 70,
        64, 64, 70, 70, 206, 207, 90, 252, 98, 69,
        70, 70, 70, 70, 98, 70, 222, 91, 92, 93,
        94, 91, 92, 93, 94, 9, 10, 11, 69, 52,
        69, 223, 92, 93, 94, 113, 1, 2, 3, 224,
        60, 4, 226, 13, 5, 61, 96, 6, 7, 65,
        65, 65, 65, 113, 235, 8, 9, 10, 11, 203,
        12, 59, 153, 154, 4, 243, 4, 5, 13, 5,
        116, 7, 251, 7, 68, 68, 68, 68, 212, 213,
        66, 59, 59, 12, 255, 12, 4, 4, 67, 5,
        5, 257, 303, 7, 7, 256, 113, 4, 258, 66,
        5, 66, 263, 266, 7, 12, 12, 67, 267, 67,
        27, 48, 42, 270, 273, 53, 12, 274, 276, 8,
        9, 10, 11, 27, 52, 42, 27, 201, 42, 277,
        208, 209, 210, 211, 8, 9, 10, 11, 275, 52,
        278, 279, 214, 215, 216, 280, 281, 282, 283, 286,
        290, 291, 292, 293, 299, 294, 1, 295, 296, 297,
        298, 302, 98, 124, 61, 107, 36, 64, 179, 180,
        170, 225, 230, 27, 187, 42, 59, 177, 234, 142,
        60, 4, 233, 149, 5, 0, 0, 0, 7, 129,
        0, 0, 0, 0, 0, 8, 9, 10, 11, 0,
        12, 59, 0, 0, 0, 115, 4, 0, 0, 5,
        0, 0, 0, 7, 0, 0, 0, 0, 0, 0,
        8, 9, 10, 11, 0, 12, 0, 0, 0, 0,
        129, 129, 0, 129, 0, 0, 0, 0, 69, 69,
        69, 69, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 66,
        66, 66, 66, 0, 0, 0, 0, 67, 67, 67,
        67,
    };
  }

  static short yycheck[];
  static {
    yycheck();
  }

  static void yycheck() {
    yycheck = new short[] { 40,
        0, 44, 41, 41, 45, 40, 41, 125, 44, 40,
        45, 44, 24, 125, 45, 40, 125, 45, 123, 40,
        218, 40, 130, 123, 44, 0, 49, 44, 256, 13,
        76, 41, 263, 256, 256, 41, 279, 280, 44, 0,
        256, 41, 125, 43, 44, 45, 280, 263, 279, 150,
        0, 279, 13, 79, 280, 16, 61, 280, 280, 59,
        60, 84, 62, 280, 150, 0, 41, 44, 43, 44,
        45, 86, 87, 119, 182, 0, 0, 275, 13, 63,
        123, 16, 59, 0, 59, 60, 41, 62, 44, 13,
        264, 41, 16, 43, 44, 45, 13, 123, 123, 16,
        40, 299, 63, 59, 123, 191, 123, 270, 271, 59,
        60, 41, 62, 281, 282, 283, 41, 40, 41, 44,
        40, 40, 45, 123, 139, 125, 45, 142, 63, 41,
        41, 280, 44, 280, 59, 61, 45, 44, 239, 63,
        241, 43, 79, 45, 245, 44, 63, 248, 123, 139,
        125, 237, 142, 239, 259, 241, 256, 275, 123, 245,
        41, 0, 248, 275, 44, 58, 275, 61, 265, 181,
        262, 263, 44, 123, 13, 125, 44, 16, 44, 60,
        279, 62, 59, 120, 121, 41, 123, 44, 274, 44,
        60, 123, 62, 276, 277, 278, 61, 280, 123, 280,
        125, 302, 262, 263, 60, 266, 62, 37, 44, 44,
        44, 40, 42, 256, 41, 256, 302, 47, 261, 41,
        44, 264, 163, 164, 63, 268, 262, 263, 269, 262,
        263, 256, 270, 271, 269, 256, 275, 280, 269, 280,
        281, 282, 283, 123, 58, 280, 281, 282, 283, 280,
        281, 282, 283, 281, 282, 283, 256, 257, 258, 279,
        260, 261, 262, 263, 264, 275, 123, 267, 268, 274,
        270, 271, 272, 273, 58, 275, 276, 277, 278, 44,
        280, 256, 257, 258, 125, 260, 261, 262, 263, 264,
        125, 125, 267, 268, 44, 270, 271, 272, 273, 123,
        275, 276, 277, 278, 44, 280, 256, 257, 258, 125,
        260, 261, 262, 263, 264, 270, 271, 267, 268, 41,
        270, 271, 272, 273, 275, 275, 276, 277, 278, 44,
        280, 256, 257, 258, 59, 260, 261, 262, 263, 264,
        270, 271, 267, 268, 151, 152, 269, 256, 274, 41,
        275, 276, 277, 278, 280, 280, 256, 280, 281, 282,
        283, 280, 281, 282, 283, 276, 277, 278, 60, 280,
        62, 44, 281, 282, 283, 58, 256, 257, 258, 125,
        274, 261, 125, 123, 264, 125, 280, 267, 268, 270,
        271, 272, 273, 76, 44, 275, 276, 277, 278, 256,
        280, 256, 272, 273, 261, 41, 261, 264, 123, 264,
        125, 268, 44, 268, 270, 271, 272, 273, 157, 158,
        41, 256, 256, 280, 44, 280, 261, 261, 41, 264,
        264, 41, 256, 268, 268, 44, 119, 261, 256, 60,
        264, 62, 44, 44, 268, 280, 280, 60, 44, 62,
        0, 1, 0, 44, 44, 5, 280, 41, 44, 275,
        276, 277, 278, 13, 280, 13, 16, 144, 16, 41,
        153, 154, 155, 156, 275, 276, 277, 278, 59, 280,
        263, 263, 159, 160, 161, 263, 263, 263, 263, 263,
        44, 44, 44, 44, 59, 44, 0, 44, 44, 44,
        44, 41, 58, 44, 41, 55, 123, 16, 123, 128,
        118, 174, 181, 63, 138, 63, 256, 121, 185, 87,
        260, 261, 182, 96, 264, -1, -1, -1, 268, 79,
        -1, -1, -1, -1, -1, 275, 276, 277, 278, -1,
        280, 256, -1, -1, -1, 260, 261, -1, -1, 264,
        -1, -1, -1, 268, -1, -1, -1, -1, -1, -1,
        275, 276, 277, 278, -1, 280, -1, -1, -1, -1,
        120, 121, -1, 123, -1, -1, -1, -1, 270, 271,
        272, 273, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 270,
        271, 272, 273, -1, -1, -1, -1, 270, 271, 272,
        273,
    };
  }

  final static short YYFINAL = 15;
  final static short YYMAXTOKEN = 283;
  final static String yyname[] = {
      "end-of-file", null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, "'%'", null, null, "'('", "')'", "'*'", "'+'",
      "','", "'-'", null, "'/'", null, null, null, null, null, null, null, null, null, null, "':'",
      "';'", "'<'", "'='", "'>'", null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, "'{'", null, "'}'", null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, "CLASS", "INTERFACE", "IMPLEMENT",
      "RETURN", "IF", "ELSE", "END_IF", "FOR", "IN", "RANGE", "IMPL", "PRINT", "TOD",
      "EQUAL_OPERATOR", "NOT_EQUAL_OPERATOR", "GREATER_THAN_OR_EQUAL_OPERATOR",
      "LESS_THAN_OR_EQUAL_OPERATOR", "MINUS_ASSIGN", "VOID", "LONG", "UINT", "DOUBLE",
      "CADENA", "ID", "CTE_DOUBLE", "CTE_UINT", "CTE_LONG",
  };
  final static String yyrule[] = {
      "$accept : program",
      "program : type_declarations",
      "type_declarations : type_declaration",
      "type_declarations : type_declarations type_declaration",
      "type_declaration : class_declaration",
      "type_declaration : interface_declaration",
      "type_declaration : implement_for_declaration",
      "type_declaration : block_statement",
      "class_declaration : CLASS ID class_body",
      "class_declaration : CLASS ID interfaces class_body",
      "class_declaration : error ID class_body",
      "class_body : '{' class_body_declarations '}'",
      "class_body_declarations : class_body_declaration",
      "class_body_declarations : class_body_declarations class_body_declaration",
      "class_body_declaration : class_member_declaration",
      "class_member_declaration : field_declaration",
      "class_member_declaration : method_declaration",
      "field_declaration : type variable_declarators ','",
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
      "method_declarator : ID '{' formal_parameter '}'",
      "method_declarator : ID '(' formal_parameter ',' error ')'",
      "method_declarator : ID '(' ')'",
      "method_declarator : ID '{' '}'",
      "method_body : block",
      "method_body : ','",
      "formal_parameter : type variable_declarator_id",
      "real_parameter : arithmetic_operation",
      "interfaces : IMPLEMENT interface_type_list",
      "interface_type_list : type_name",
      "interface_type_list : interface_type_list ';' type_name",
      "interface_declaration : INTERFACE ID interface_body",
      "interface_declaration : INTERFACE ID error ','",
      "interface_body : '{' interface_member_declaration '}'",
      "interface_body : '(' interface_member_declaration ')'",
      "interface_body : '{' '}'",
      "interface_body : '(' ')'",
      "interface_member_declaration : interface_method_declaration",
      "interface_member_declaration : interface_member_declaration interface_method_declaration",
      "interface_method_declaration : result_type method_declarator",
      "interface_method_declaration : result_type error ','",
      "implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
      "implement_for_declaration : IMPL FOR reference_type ':' error ','",
      "implement_for_declaration : IMPL FOR error ':' implement_for_body ','",
      "implement_for_declaration : error reference_type ':' implement_for_body",
      "implement_for_body : '{' implement_for_body_declarations '}'",
      "implement_for_body_declarations : implement_for_body_declaration",
      "implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
      "implement_for_body_declaration : implement_for_method_declaration",
      "implement_for_method_declaration : method_header implement_for_method_body",
      "implement_for_method_body : method_body",
      "assignment : left_hand_side assignment_operator arithmetic_operation",
      "left_hand_side : reference_type",
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
      "unary_expression : ID",
      "term : factor",
      "term : '(' expression ')'",
      "term : '(' ')'",
      "factor : CTE_DOUBLE",
      "factor : CTE_UINT",
      "factor : CTE_LONG",
      "factor : '-' CTE_DOUBLE",
      "factor : '-' CTE_LONG",
      "factor : '-' CTE_UINT",
      "assignment_operator : '='",
      "assignment_operator : MINUS_ASSIGN",
      "method_invocation : ID '(' real_parameter ')' ','",
      "method_invocation : ID '(' ')' ','",
      "method_invocation : ID '(' real_parameter ',' error ')' ','",
      "type : primitive_type",
      "type : reference_type",
      "primitive_type : numeric_type",
      "reference_type : ID",
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
      "executable_block : '{' executable_block_statements '}'",
      "executable_block : '{' '}'",
      "block_statements : block_statement",
      "block_statements : block_statements block_statement",
      "executable_block_statements : executable_statament",
      "executable_block_statements : executable_block_statements executable_statament",
      "block_statement : local_variable_declaration_statement",
      "block_statement : statement",
      "executable_statament : if_then_statement",
      "executable_statament : if_then_else_statement",
      "executable_statament : for_in_range_statement",
      "executable_statament : print_statement",
      "executable_statament : expression_statement",
      "executable_statament : empty_statement",
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
      "if_then_statement : IF '(' expression ')' executable_block END_IF ','",
      "if_then_statement : IF '(' expression ')' executable_statament END_IF ','",
      "if_then_statement : IF '(' expression ')' executable_statament ','",
      "if_then_statement : IF '(' expression ')' executable_block ','",
      "if_then_statement : IF '(' expression ')' error END_IF ','",
      "if_then_statement : IF '(' error ')' executable_block END_IF ','",
      "if_then_statement : IF '(' error ')' executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block error ','",
      "if_then_else_statement : IF '(' error ')' executable_block ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' error ')' executable_block ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' error ')' executable_statament ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' error ')' executable_statament ELSE executable_block END_IF ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statament",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ','",
      "for_in_range_statement : FOR for_variable IN RANGE error ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_statament",
      "for_variable : reference_type",
      "for_init : factor",
      "for_update : factor",
      "for_end : factor",
      "print_statement : PRINT CADENA ','",
      "print_statement : PRINT error ','",
      "print_statement : error CADENA ','",
  };

  // #line 376 "grammer.y"

  private static AnalizadorLexico aLexico;
  private static int yylval_recognition = 0;
  public static boolean error = false;

  // This method is the one where BYACC/J expects to obtain its input tokens.
  // Wrap any file/string scanning code you have in this function. This method
  // should return <0 if there is an error, and 0 when it encounters the end of
  // input. See the examples to clarify what we mean.
  int yylex() {
    Tupla<String, Short> t = aLexico.generateToken();
    String lexema = t.getFirst();
    Short token = t.getSecond();

    if (lexema != null) {
      yylval = new ParserVal(lexema);
      yylval_recognition += 1;
    }

    return token;
  }

  // This method is expected by BYACC/J, and is used to provide error messages to
  // be directed to the channels the user desires.
  void yyerror(String msg) {
    System.out.println("Error en el parser: " + msg);
  }

  // ###############################################################
  // metodos auxiliares a la gramatica
  // ###############################################################

  private String negarDouble(String lexema) {

    double RDN_MIN = -2.2250738585072014D * -Math.pow(10, 308);
    double RDN_MAX = -1.7976931348623157D * Math.pow(10, 308);

    String n_lexema = '-' + lexema;
    double numero = 0.0;

    try {
      numero = Double.parseDouble(n_lexema);
    } catch (Exception ex) {
    }

    if (numero > RDN_MAX || numero < RDN_MIN) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      n_lexema = "" + RDN_MAX;
    }

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
  }

  private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

    if (!TablaSimbolos.containsKey(n_lexema)) {

      if (tipo == "D") { // Perdon Luis por hacer un if por tipos T_T
        TablaSimbolos.addDouble(n_lexema);

      } else {
        TablaSimbolos.addLong(n_lexema);
      }

      TablaSimbolos.addContador(n_lexema);
      TablaSimbolos.decreaseCounter(lexema);

    } else {
      TablaSimbolos.increaseCounter(n_lexema);
    }
  }

  private String negarLong(String lexema) {

    String n_lexema = '-' + lexema;
    long numero = 0;

    try {
      numero = Long.parseLong(n_lexema);
    } catch (Exception ex) {
    }

    addTablaSimbolos(lexema, n_lexema, "L");

    return n_lexema;
  }

  // ###############################################################
  // metodos de lectura de los programadas
  // ###############################################################

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

  public static void main(String[] args) throws IOException {
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if (!aLexico.hasReadWell()) {
      return;
    }
    System.out.println(aLexico.getProgram());

    Parser aSintactico = new Parser();
    aSintactico.run();
    aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
  }

  // #line 806 "Parser.java"
  // ###############################################################
  // method: yylexdebug : check lexer state
  // ###############################################################
  void yylexdebug(int state, int ch) {
    String s = null;
    if (ch < 0)
      ch = 0;
    if (ch <= YYMAXTOKEN) // check index bounds
      s = yyname[ch]; // now get it
    if (s == null)
      s = "illegal-symbol";
    debug("state " + state + ", reading " + ch + " (" + s + ")");
  }

  // The following are now global, to aid in error reporting
  int yyn; // next next thing to do
  int yym; //
  int yystate; // current parsing state from state table
  String yys; // current token string

  // ###############################################################
  // method: yyparse : parse input and execute indicated items
  // ###############################################################
  int yyparse() {
    boolean doaction;
    init_stacks();
    yynerrs = 0;
    yyerrflag = 0;
    yychar = -1; // impossible char forces a read
    yystate = 0; // initial state
    state_push(yystate); // save it
    val_push(yylval); // save empty value
    while (true) // until parsing is done, either correctly, or w/error
    {
      doaction = true;
      if (yydebug)
        debug("loop");
      // #### NEXT ACTION (from reduction table)
      for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
        if (yydebug)
          debug("yyn:" + yyn + "  state:" + yystate + "  yychar:" + yychar);
        if (yychar < 0) // we want a char?
        {
          yychar = yylex(); // get next token
          if (yydebug)
            debug(" next yychar:" + yychar);
          // #### ERROR CHECK ####
          if (yychar < 0) // it it didn't work/error
          {
            yychar = 0; // change it to default string (no -1!)
            if (yydebug)
              yylexdebug(yystate, yychar);
          }
        } // yychar<0
        yyn = yysindex[yystate]; // get amount to shift by (shift index)
        if ((yyn != 0) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar) {
          if (yydebug)
            debug("state " + yystate + ", shifting to state " + yytable[yyn]);
          // #### NEXT STATE ####
          yystate = yytable[yyn];// we are in a new state
          state_push(yystate); // save it
          val_push(yylval); // push our lval as the input for next rule
          yychar = -1; // since we have 'eaten' a token, say we need another
          if (yyerrflag > 0) // have we recovered an error?
            --yyerrflag; // give ourselves credit
          doaction = false; // but don't process yet
          break; // quit the yyn=0 loop
        }

        yyn = yyrindex[yystate]; // reduce
        if ((yyn != 0) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar) { // we reduced!
          if (yydebug)
            debug("reduce");
          yyn = yytable[yyn];
          doaction = true; // get ready to execute
          break; // drop down to actions
        } else // ERROR RECOVERY
        {
          if (yyerrflag == 0) {
            yyerror("syntax error");
            yynerrs++;
          }
          if (yyerrflag < 3) // low error count?
          {
            yyerrflag = 3;
            while (true) // do until break
            {
              if (stateptr < 0) // check for under & overflow here
              {
                yyerror("stack underflow. aborting..."); // note lower case 's'
                return 1;
              }
              yyn = yysindex[state_peek(0)];
              if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                  yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE) {
                if (yydebug)
                  debug("state " + state_peek(0) + ", error recovery shifting to state " + yytable[yyn] + " ");
                yystate = yytable[yyn];
                state_push(yystate);
                val_push(yylval);
                doaction = false;
                break;
              } else {
                if (yydebug)
                  debug("error recovery discarding state " + state_peek(0) + " ");
                if (stateptr < 0) // check for under & overflow here
                {
                  yyerror("Stack underflow. aborting..."); // capital 'S'
                  return 1;
                }
                state_pop();
                val_pop();
              }
            }
          } else // discard this token
          {
            if (yychar == 0)
              return 1; // yyabort
            if (yydebug) {
              yys = null;
              if (yychar <= YYMAXTOKEN)
                yys = yyname[yychar];
              if (yys == null)
                yys = "illegal-symbol";
              debug("state " + yystate + ", error recovery discards token " + yychar + " (" + yys + ")");
            }
            yychar = -1; // read another
          }
        } // end error recovery
      } // yyn=0 loop
      if (!doaction) // any reason not to proceed?
        continue; // skip action
      yym = yylen[yyn]; // get count of terminals on rhs
      if (yydebug)
        debug("state " + yystate + ", reducing " + yym + " by rule " + yyn + " (" + yyrule[yyn] + ")");
      if (yym > 0) // if count of rhs not 'nil'
        yyval = val_peek(yym - 1); // get current semantic value
      yyval = dup_yyval(yyval); // duplicate yyval if ParserVal is used as semantic value
      switch (yyn) {
        // ########## USER-SUPPLIED ACTIONS ##########
        case 1:
        // #line 31 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa.");
        }
          break;
        case 8:
        // #line 52 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");
        }
          break;
        case 9:
        // #line 53 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");
        }
          break;
        case 10:
        // #line 54 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de CLASS no valida.");
        }
          break;
        case 17:
        // #line 71 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");
        }
          break;
        case 27:
        // #line 97 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");
        }
          break;
        case 28:
        // #line 98 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");
        }
          break;
        case 29:
        // #line 99 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");
        }
          break;
        case 30:
        // #line 100 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");
        }
          break;
        case 31:
        // #line 101 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");
        }
          break;
        case 39:
        // #line 123 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");
        }
          break;
        case 40:
        // #line 124 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la interface.");
        }
          break;
        case 42:
        // #line 128 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");
        }
          break;
        case 44:
        // #line 130 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");
        }
          break;
        case 48:
        // #line 138 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario generar la declaracion del metodo");
        }
          break;
        case 49:
        // #line 141 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");
        }
          break;
        case 50:
        // #line 142 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");
        }
          break;
        case 51:
        // #line 143 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");
        }
          break;
        case 52:
        // #line 144 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida.");
        }
          break;
        case 59:
        // #line 168 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");
        }
          break;
        case 61:
        // #line 174 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");
        }
          break;
        case 70:
        // #line 189 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");
        }
          break;
        case 83:
        // #line 210 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");
        }
          break;
        case 87:
        // #line 216 "grammer.y"
        {
          System.out.println("Posicion 1: " + val_peek(1).sval + ", Posicion 2: " + val_peek(0).sval);
          yyval = new ParserVal(negarDouble(val_peek(0).sval));
        }
          break;
        case 88:
        // #line 217 "grammer.y"
        {
          System.out.println(val_peek(0).sval);
          yyval = new ParserVal(negarLong(val_peek(0).sval));
        }
          break;
        case 89:
        // #line 218 "grammer.y"
        {
          Logger.logWarning(aLexico.getProgramPosition(), "Los tipos enteros deben ser sin signo.");
          yyval = new ParserVal(val_peek(0).sval);
        }
          break;
        case 92:
        // #line 225 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");
        }
          break;
        case 93:
        // #line 226 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");
        }
          break;
        case 94:
        // #line 227 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");
        }
          break;
        case 106:
        // #line 269 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 108:
        // #line 271 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 123:
        // #line 299 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");
        }
          break;
        case 138:
        // #line 330 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");
        }
          break;
        case 139:
        // #line 331 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");
        }
          break;
        case 140:
        // #line 332 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el final de la sentencia de control IF.");
        }
          break;
        case 141:
        // #line 333 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el final de la sentencia de control IF.");
        }
          break;
        case 142:
        // #line 334 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el cuerpo de la sentencia de control IF.");
        }
          break;
        case 143:
        // #line 335 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 144:
        // #line 336 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 145:
        // #line 340 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 146:
        // #line 341 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 147:
        // #line 342 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 148:
        // #line 343 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 149:
        // #line 344 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el END_IF de la sentencia de control IF.");
        }
          break;
        case 150:
        // #line 345 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 151:
        // #line 346 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 152:
        // #line 347 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 153:
        // #line 348 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 154:
        // #line 352 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
        }
          break;
        case 155:
        // #line 353 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
        }
          break;
        case 156:
        // #line 354 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");
        }
          break;
        case 157:
        // #line 355 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");
        }
          break;
        case 158:
        // #line 356 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");
        }
          break;
        case 163:
        // #line 371 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");
        }
          break;
        case 164:
        // #line 372 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");
        }
          break;
        case 165:
        // #line 373 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");
        }
          break;
        // #line 1179 "Parser.java"
        // ########## END OF USER-SUPPLIED ACTIONS ##########
      }// switch
       // #### Now let's reduce... ####
      if (yydebug)
        debug("reduce");
      state_drop(yym); // we just reduced yylen states
      yystate = state_peek(0); // get new state
      val_drop(yym); // corresponding value drop
      yym = yylhs[yyn]; // select next TERMINAL(on lhs)
      if (yystate == 0 && yym == 0)// done? 'rest' state and at first TERMINAL
      {
        if (yydebug)
          debug("After reduction, shifting from state 0 to state " + YYFINAL + "");
        yystate = YYFINAL; // explicitly say we're done
        state_push(YYFINAL); // and save it
        val_push(yyval); // also save the semantic value of parsing
        if (yychar < 0) // we want another character?
        {
          yychar = yylex(); // get next character
          if (yychar < 0)
            yychar = 0; // clean, if necessary
          if (yydebug)
            yylexdebug(yystate, yychar);
        }
        if (yychar == 0) // Good exit (if lex returns 0 ;-)
          break; // quit the loop--all DONE
      } // if yystate
      else // else not done yet
      { // get next state and push, for next yydefred[]
        yyn = yygindex[yym]; // find out where to go
        if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
          yystate = yytable[yyn]; // get new state
        else
          yystate = yydgoto[yym]; // else go to new defred
        if (yydebug)
          debug("after reduction, shifting from state " + state_peek(0) + " to state " + yystate + "");
        state_push(yystate); // going again, so push state & val...
        val_push(yyval); // for next action
      }
    } // main loop
    return 0;// yyaccept!!
  }
  // ## end of method parse() ######################################

  // ## run() --- for Thread #######################################
  /**
   * A default run method, used for operating this parser
   * object in the background. It is intended for extending Thread
   * or implementing Runnable. Turn off with -Jnorun .
   */
  public void run() {
    yyparse();
  }
  // ## end of method run() ########################################

  // ## Constructors ###############################################
  /**
   * Default constructor. Turn off with -Jnoconstruct .
   * 
   */
  public Parser() {
    // nothing to do
  }

  /**
   * Create a parser, setting the debug to true or false.
   * 
   * @param debugMe true for debugging, false for no debug.
   */
  public Parser(boolean debugMe) {
    yydebug = debugMe;
  }
  // ###############################################################

}
// ################### END OF CLASS ##############################
