using System;
using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Text;
using System.Xml;
using System.Globalization;
using System.IO;

namespace etata_database_gui
{
	/// <summary>
	/// Summary description for XmlDatabase
	/// </summary>
	public class XmlDatabase
	{
		#region Xml_Constants
		private const string V_TRUE = "true";
		private const string V_FALSE = "false";
        //--

        private const string ELEM_LOCALE = "locale";
        private const string ELEM_STRINGS = "strings";
        private const string ELEM_ITEM = "item";
        private const string ELEM_ITEMS = "items";
        private const string ELEM_DATA = "data";

        private const string ATR_LANGUAGE = "language";
        private const string ATR_AUTHOR = "author";
        private const string ATR_CREATED = "created";
        private const string ATR_LASTCHANGE = "lastchange";
        private const string ATR_KEY = "key";
        private const string ATR_NAME = "name";
        private const string ATR_DANGERLEVEL = "status";
        private const string ATR_FUNCTION = "function";
        private const string ATR_FOOD = "food";
        private const string ATR_SIDEFX = "warn";
        private const string ATR_DETAILS = "info";
        private const string ATR_VEGETARIANS = "veg";

        #endregion

        public enum ATTRIBUTES
        {
            ATTRIB_NAME = 0,
            ATTRIB_DANGERLEVEL = 1,
            ATTRIB_FUNCTION = 2,
            ATTRIB_FOOD = 3,
            ATTRIB_SIDEFX = 4,
            ATTRIB_DETAILS = 5,
            ATTRIB_VEGETARIANS = 6
        };

        public const int mergeTexts = 0x00000001;
        public const int mergeName = 0x00000002;
        public const int mergeDangerLevel = 0x00000004;
        public const int mergeFunction = 0x00000008;
        public const int mergeFood = 0x00000010;
        public const int mergeSideFx = 0x00000020;
        public const int mergeDetails = 0x00000040;
        public const int mergeVegetarians = 0x00000080;

        public const int COUNT_ATTRIBS = 7;
        public const int ATTRIB_NAME = 0;
        public const int ATTRIB_DANGERLEVEL = 1;
        public const int ATTRIB_FUNCTION = 2;
        public const int ATTRIB_FOOD = 3;
        public const int ATTRIB_SIDEFX = 4;
        public const int ATTRIB_DETAILS = 5;
        public const int ATTRIB_VEGETARIANS = 6;

        //--
		private Encoding	s_curEncoding = Encoding.UTF8;
        //---
        private string s_language = string.Empty;
        private string s_author = string.Empty;
        private string s_date_crated = string.Empty;
        private string s_date_lastchange = string.Empty;
        private SortedList h_strings = new SortedList(100);
        //private Hashtable h_items = new Hashtable(1000);
        private SortedList<string, string[]> h_items = new SortedList<string, string[]>(1000);
        private ArrayList ar_deleted = new ArrayList(100);
        private string s_filename = string.Empty;

		#region Properties
        public SortedList strings
        {
            get
            {
                return h_strings;
            }
        }

        public SortedList<string, string[]> items
        {
            get
            {
                return h_items;
            }
        }

        public string DateCreated
        {
            get
            {
                return s_date_crated;
            }
            set
            {
                s_date_crated = value;
            }
        }

        public string DateLastChange
        {
            get
            {
                return s_date_lastchange;
            }
        }

		public string Author
		{
			get
			{
				return s_author;
			}
            set
            {
                s_author = value;
            }
		}

		public string Language
		{
			get
			{
				return s_language;
			}
            set
            {
                s_language = value;
            }
		}

        public int sizeStrings
        {
            get
            {
                return h_strings.Count;
            }
        }

        public int sizeItems
        {
            get
            {
                return h_items.Count;
            }
        }

		public System.Text.Encoding DataEncoding 
		{
			get
			{
				return s_curEncoding;
			}
		}
		#endregion

		public XmlDatabase( string strFilePath )
		{
			load( strFilePath );
		}

        public void delete(string key)
        {
            if (h_strings[key] != null || h_items[key] != null)
                ar_deleted.Add(key);
        }

        public bool isDeleted(string key)
        {
            foreach (String item in ar_deleted)
            {
                if (item == key)
                    return true;
            }
            return false;
        }

        public bool undelete(string key)
        {
            foreach (String item in ar_deleted)
            {
                if (item == key)
                {
                    ar_deleted.Remove(item);
                    return true;
                }
            }
            return false;
        }

		public string getString( string key )
		{
            if ( h_strings[key] != null )
                return (string)h_strings[key];

            return null;
		}

        public void setString(string key, string value)
        {
            if (h_strings[key] != null)
                h_strings[key] = value;
            else
                h_strings.Add(key, value);
        }

		public string[] getItem( string key )
		{
            return (string[])h_items[key];
		}

        public string getItemValue(string key, ATTRIBUTES attrib)
        {
            string[] values = (string[])h_items[key];
            return (string)values[(int)attrib];
        }

        public void setItemValue(string key, ATTRIBUTES attrib, string value)
        {
            
            if (h_items.ContainsKey(key) && h_items[key] != null)
            {
                string[] values = (string[])h_items[key];
                values[(int)attrib] = value;
                h_items[key] = values;
            }
            else
            {
                string[] values = new string[COUNT_ATTRIBS];
                values[(int)attrib] = value;
                h_items.Add(key, values);
            }
        }

        public void saveAsCSV(string strFilePath)
        {
            //try
            {
                StreamWriter sw = new StreamWriter(strFilePath, false, Encoding.UTF8);
                const string separator = ";";

                // save strings
                foreach (String key in h_strings.Keys)
                {
                    sw.Write(key);
                    sw.Write(separator);
                    sw.WriteLine((string)h_strings[key]);
                }
                // save items
                foreach (String key in h_items.Keys)
                {
                    String[] attribs = this.getItem(key);
                    sw.Write(key); sw.Write(separator);
                    sw.Write(attribs[ATTRIB_NAME].Replace(";", "&#59")); sw.Write(separator);
                    sw.Write(attribs[ATTRIB_DANGERLEVEL].Replace(";", "&#59")); sw.Write(separator);
                    sw.Write(attribs[ATTRIB_VEGETARIANS].Replace(";", "&#59")); sw.Write(separator);
                    sw.Write(attribs[ATTRIB_FUNCTION].Replace(";", "&#59")); sw.Write(separator);
                    sw.Write(attribs[ATTRIB_FOOD].Replace(";", "&#59")); sw.Write(separator);
                    sw.Write(attribs[ATTRIB_SIDEFX].Replace(";", "&#59")); sw.Write(separator);
                    sw.WriteLine(attribs[ATTRIB_DETAILS].Replace(";", "&#59")); 
                }

                sw.Close();
            }
            //catch (Exception ex)
            //{
            //    throw new Exception(
            //        String.Format("Error occured - Exception Info: {0}", ex.ToString())
            //        );
            //}

        }

        public void save(string strFilePath)
        {
            //try
            {
                XmlDocument xmlDoc = new XmlDocument();
                //xmlDoc.Load(this.s_filename);
                xmlDoc.LoadXml(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<locale language=\"UNKNOWN\" author=\"UNKNOWN\" created=\"\" lastchange=\"UNKNOWN\">" +
  "<strings></strings><items></items>" +
  "</locale>"
                    );

                XmlNode nodeRoot = xmlDoc.DocumentElement;

                ((XmlElement)nodeRoot).SetAttribute(ATR_AUTHOR, s_author);
                if (s_date_crated.Length == 0)
                {
                    ((XmlElement)nodeRoot).SetAttribute(ATR_CREATED,
                        Convert.ToDateTime(DateTime.Now).ToUniversalTime().ToString(new CultureInfo("en-US")));
                }
                else
                {
                    ((XmlElement)nodeRoot).SetAttribute(ATR_CREATED,
                        Convert.ToDateTime(s_date_crated).ToUniversalTime().ToString(new CultureInfo("en-US")));
                }

                ((XmlElement)nodeRoot).SetAttribute(ATR_LASTCHANGE,
                        Convert.ToDateTime(DateTime.Now).ToUniversalTime().ToString(new CultureInfo("en-US")));
                ((XmlElement)nodeRoot).SetAttribute(ATR_LANGUAGE, s_language);

                
                // save strings
                foreach (String key in h_strings.Keys)
                {
                    XmlNode node = nodeRoot.SelectSingleNode(
                        String.Format("/locale/strings/item[@key = \"{0}\"]",
                        key)
                        );
                    if (node == null) // this must be a new item
                    {
                        if (! isDeleted(key))
                        {
                            XmlElement newStringNode = xmlDoc.CreateElement(ELEM_ITEM);
                            newStringNode.SetAttribute(ATR_KEY, key);
                            newStringNode.AppendChild(xmlDoc.CreateTextNode((string)h_strings[key]));
                            nodeRoot.SelectSingleNode("/locale/strings").AppendChild(newStringNode);
                        }
                    }
                    else
                    {
                        if (isDeleted(key)) // remove from tree if deleted
                            node.ParentNode.RemoveChild(node);
                        else
                            node.FirstChild.Value = (string)h_strings[key];
                    }
                }

                // sort items by number only !!!
                List<KeyValuePair<string, string[]>> l = new List<KeyValuePair<string, string[]>>(h_items);
                l.Sort(delegate(KeyValuePair<string, string[]> k1, KeyValuePair<string, string[]> k2) {
                    Regex rx = new Regex("([0-9]+)", RegexOptions.IgnoreCase);
                    MatchCollection m = rx.Matches(k1.Key);
                    int v1 = Convert.ToInt32(m[0].Value);
                    m = rx.Matches(k2.Key);
                    int v2 = Convert.ToInt32(m[0].Value);

                    if (v1 == v2)
                        return k1.Key.CompareTo(k2.Key);
                    //System.Diagnostics.Debug.WriteLine(string.Format("K1={0}, K2={1}", v1, v2));
                    return v1 - v2;
                });
                
                // save items
                foreach (KeyValuePair<string, string[]> k in l)
                {
                    string key = k.Key;

                    XmlNode node = nodeRoot.SelectSingleNode(
                        String.Format("/locale/items/item[@key = \"{0}\"]",
                        key)
                        );

                    String[] attribs = this.getItem(key);

                    System.Diagnostics.Debug.WriteLine("Saving key=" + key);

                    if (node == null) // this must be a new item
                    {
                        if (!isDeleted(key))
                        {
                            XmlElement newStringNode = xmlDoc.CreateElement(ELEM_ITEM);
                            newStringNode.SetAttribute(ATR_KEY, key);
                            newStringNode.SetAttribute(ATR_NAME, attribs[ATTRIB_NAME]);
                            newStringNode.SetAttribute(ATR_DANGERLEVEL, attribs[ATTRIB_DANGERLEVEL]);
                            newStringNode.SetAttribute(ATR_VEGETARIANS, attribs[ATTRIB_VEGETARIANS]);

                            XmlElement dataNode = xmlDoc.CreateElement(ELEM_DATA);
                            dataNode.SetAttribute(ATR_KEY, ATR_FUNCTION);
                            dataNode.AppendChild(xmlDoc.CreateTextNode(attribs[ATTRIB_FUNCTION]));
                            newStringNode.AppendChild(dataNode);

                            dataNode = xmlDoc.CreateElement(ELEM_DATA);
                            dataNode.SetAttribute(ATR_KEY, ATR_FOOD);
                            dataNode.AppendChild(xmlDoc.CreateCDataSection(attribs[ATTRIB_FOOD]));
                            newStringNode.AppendChild(dataNode);

                            dataNode = xmlDoc.CreateElement(ELEM_DATA);
                            dataNode.SetAttribute(ATR_KEY, ATR_SIDEFX);
                            dataNode.AppendChild(xmlDoc.CreateCDataSection(attribs[ATTRIB_SIDEFX]));
                            newStringNode.AppendChild(dataNode);

                            dataNode = xmlDoc.CreateElement(ELEM_DATA);
                            dataNode.SetAttribute(ATR_KEY, ATR_DETAILS);
                            dataNode.AppendChild(xmlDoc.CreateCDataSection(attribs[ATTRIB_DETAILS]));
                            newStringNode.AppendChild(dataNode);

                            // add new <item>
                            nodeRoot.SelectSingleNode("/locale/items").AppendChild(newStringNode);
                        }
                    }
                    else
                    {
                        if (isDeleted(key)) // delete child if in list
                        {
                            node.ParentNode.RemoveChild(node);
                        }
                        else
                        {
                            // edit existing item
                            for (int i = 0; i < 5; i++)
                            {
                                System.Diagnostics.Debug.WriteLine(String.Format("Attrib[{0}]={1}", i, attribs[i]));
                            }

                            node.Attributes[ATR_NAME].Value = attribs[ATTRIB_NAME];
                            node.Attributes[ATR_DANGERLEVEL].Value = attribs[ATTRIB_DANGERLEVEL];
                            node.Attributes[ATR_VEGETARIANS].Value = attribs[ATTRIB_VEGETARIANS];
                            
                            XmlNode child = null;

                            child = node.SelectSingleNode(String.Format("{0}[@key=\"{1}\"]", ELEM_DATA, ATR_FUNCTION)).FirstChild;
                            if ( child != null )
                                child.Value = attribs[ATTRIB_FUNCTION];

                            child = node.SelectSingleNode(String.Format("{0}[@key=\"{1}\"]", ELEM_DATA, ATR_FOOD)).FirstChild;
                            if ( child != null )
                                child.Value = attribs[ATTRIB_FOOD];

                            child = node.SelectSingleNode(String.Format("{0}[@key=\"{1}\"]", ELEM_DATA, ATR_SIDEFX)).FirstChild;
                            if (child != null)
                                child.Value = attribs[ATTRIB_SIDEFX];

                            child = node.SelectSingleNode(String.Format("{0}[@key=\"{1}\"]", ELEM_DATA, ATR_DETAILS)).FirstChild;
                            if (child != null)
                                child.Value = attribs[ATTRIB_DETAILS];
                        }
                    }
                }

                // save xml document
                xmlDoc.Save(strFilePath);
			}
            //catch( XmlException XmlEx )
            //{
            //    throw new Exception( 
            //        String.Format( "Error on reading Xml content - Exception Info: {0}", XmlEx.ToString() )
            //        );
            //}
            //catch( Exception ex )
            //{
            //    throw new Exception( 
            //        String.Format( "Error occured - Exception Info: {0}", ex.ToString() )
            //        );
            //}
        }

        public void save()
        {
            this.save(this.s_filename);
        }

		public void load( string strFilePath )
		{
			//try
			{
                this.s_filename = strFilePath;

				XmlDocument xmlDoc = new XmlDocument();
				xmlDoc.Load( strFilePath );
				XmlNode nodeRoot = xmlDoc.DocumentElement;

                //// read version 

                //if ( null == nodeRoot.Attributes[ATR_LANGUAGE] )
                //    throw new Exception( String.Format("Could not read node <{0}>", ATR_VERSION) );
                //else
                //{
                //    String strVer = nodeRoot.Attributes[ATR_VERSION].Value;
                //    if ( strVer.CompareTo( VERSION ) > 0 )
                //    {
                //        throw new Exception( String.Format("Version '{0}' is higher than supported - '{1}' !", strVer, VERSION ) );
                //    }
                //}

				// read language
				if ( null == nodeRoot.Attributes[ATR_LANGUAGE] )
                    throw new Exception(String.Format("Could not read node <{0}>", ATR_LANGUAGE));
				else
				{
                    s_language = nodeRoot.Attributes[ATR_LANGUAGE].Value;
				}

				// read author
				if ( null != nodeRoot.Attributes[ATR_AUTHOR] )
                    s_author = nodeRoot.Attributes[ATR_AUTHOR].Value;
                // read created
                if (null != nodeRoot.Attributes[ATR_CREATED])
                {
                    s_date_crated = nodeRoot.Attributes[ATR_CREATED].Value;
                    if (s_date_crated.Length > 0)
                        s_date_crated = DateTime.Parse(s_date_crated, new CultureInfo("en-US")).ToString();
                }
                // read lastchange
                if (null != nodeRoot.Attributes[ATR_LASTCHANGE])
                {
                    s_date_lastchange = nodeRoot.Attributes[ATR_LASTCHANGE].Value;
                    if (s_date_lastchange.Length > 0)
                        s_date_lastchange = DateTime.Parse(s_date_lastchange, new CultureInfo("en-US")).ToString();
                    
                }


				foreach( XmlElement nodeItem in nodeRoot.ChildNodes )
				{
                    // read strings items
					if ( nodeItem.Name.Equals( ELEM_STRINGS ) )
					{
						foreach( XmlElement subItem in nodeItem.ChildNodes )
						{
							if ( subItem.Name.Equals( ELEM_ITEM  ) )
                                h_strings.Add(subItem.Attributes[ATR_KEY].Value, subItem.FirstChild != null ? subItem.FirstChild.Value : string.Empty);

                            System.Diagnostics.Debug.WriteLine("Read string=" + subItem.Attributes[ATR_KEY].Value);
						}
					}
                    else if (nodeItem.Name.Equals(ELEM_ITEMS))
                    {
                        foreach (XmlElement subItem in nodeItem.ChildNodes)
                        {
                            // read attributes first
                            String key = subItem.Attributes[ATR_KEY].Value;
                            System.Diagnostics.Debug.WriteLine("Reading item=" + key);

                            String[] ar = new string[COUNT_ATTRIBS];
                            //ar[ATTRIB_NAME] = subItem.Attributes[ATR_NAME] != null ? subItem.Attributes[ATR_NAME].Value : "";
                            ar[ATTRIB_NAME] = subItem.Attributes[ATR_NAME].Value;
                            ar[ATTRIB_DANGERLEVEL] = subItem.Attributes[ATR_DANGERLEVEL] != null ? subItem.Attributes[ATR_DANGERLEVEL].Value : "";
                            ar[ATTRIB_VEGETARIANS] = subItem.Attributes[ATR_VEGETARIANS] != null ? subItem.Attributes[ATR_VEGETARIANS].Value : "";
                            
                            // read <data> elements
                            foreach (XmlElement dataItem in subItem.ChildNodes)
                            {
                                if (dataItem.Name.Equals(ELEM_DATA))
                                {
                                    String dataKey = dataItem.Attributes[ATR_KEY].Value;
                                    System.Diagnostics.Debug.WriteLine("Read item key=" + dataKey);
                                    switch (dataKey)
                                    {
                                        case ATR_FUNCTION:
                                            ar[ATTRIB_FUNCTION] = dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty;
                                            break;
                                        case ATR_FOOD:
                                            ar[ATTRIB_FOOD] = dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty;
                                            break;
                                        case ATR_SIDEFX:
                                            ar[ATTRIB_SIDEFX] = dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty;
                                            break;
                                        case ATR_DETAILS:
                                            ar[ATTRIB_DETAILS] = dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty;
                                            break;
                                    }
                                    System.Diagnostics.Debug.WriteLine("Read Data item=" + dataKey);
                                }
                            }

                            h_items.Add(key, ar);
                        }
                    }
				}

			}
            //catch( XmlException XmlEx )
            //{
            //    throw new Exception( 
            //        String.Format( "Error on reading Xml content - Exception Info: {0}", XmlEx.ToString() )
            //        );
            //}
            //catch( Exception ex )
            //{
            //    throw new Exception( 
            //        String.Format( "Error occured - Exception Info: {0}", ex.ToString() )
            //        );
            //}
		}

        public void mergeByTagOrAttrib(string withFile, int merges)
        {
            XmlDocument xmlFromDoc = new XmlDocument();
            xmlFromDoc.Load(withFile);
            XmlNode nodeFromRoot = xmlFromDoc.DocumentElement;

            string sFromLanguage = string.Empty;

            // read language
            if (null == nodeFromRoot.Attributes[ATR_LANGUAGE])
                throw new Exception(String.Format("Could not read node <{0}>", ATR_LANGUAGE));
            else
            {
                sFromLanguage = nodeFromRoot.Attributes[ATR_LANGUAGE].Value;
            }

            foreach (XmlElement nodeItem in nodeFromRoot.ChildNodes)
            {
                // read strings items
                if (nodeItem.Name.Equals(ELEM_STRINGS))
                {
                    if ((merges & mergeTexts) == mergeTexts)
                    {
                        foreach (XmlElement subItem in nodeItem.ChildNodes)
                        {
                            if (subItem.Name.Equals(ELEM_ITEM))
                                this.setString(subItem.Attributes[ATR_KEY].Value, subItem.FirstChild != null ? subItem.FirstChild.Value : string.Empty);

                            System.Diagnostics.Debug.WriteLine("Merged string=" + subItem.Attributes[ATR_KEY].Value);
                        }
                    }
                }
                else if (nodeItem.Name.Equals(ELEM_ITEMS))
                {
                    foreach (XmlElement subItem in nodeItem.ChildNodes)
                    {
                        // read attributes first
                        String key = subItem.Attributes[ATR_KEY].Value;
                        System.Diagnostics.Debug.WriteLine("Merging item=" + key);

                        String[] ar = new string[COUNT_ATTRIBS];
                        //ar[ATTRIB_NAME] = subItem.Attributes[ATR_NAME] != null ? subItem.Attributes[ATR_NAME].Value : "";

                        // @NAME
                        if ((merges & mergeName) == mergeName)
                            setItemValue(key, ATTRIBUTES.ATTRIB_NAME, subItem.Attributes[ATR_NAME].Value);

                        // @DANGERLEVEL
                        if ((merges & mergeDangerLevel) == mergeDangerLevel)
                            setItemValue(key, ATTRIBUTES.ATTRIB_DANGERLEVEL, 
                                subItem.Attributes[ATR_DANGERLEVEL] != null ? subItem.Attributes[ATR_DANGERLEVEL].Value : "" );

                        // @VEGETARIANS
                        if ((merges & mergeVegetarians) == mergeVegetarians)
                            setItemValue(key, ATTRIBUTES.ATTRIB_VEGETARIANS, 
                                subItem.Attributes[ATR_VEGETARIANS] != null ? subItem.Attributes[ATR_VEGETARIANS].Value : "");

                        // read <data> elements
                        foreach (XmlElement dataItem in subItem.ChildNodes)
                        {
                            if (dataItem.Name.Equals(ELEM_DATA))
                            {
                                String dataKey = dataItem.Attributes[ATR_KEY].Value;
                                System.Diagnostics.Debug.WriteLine("Merging item key=" + dataKey);
                                switch (dataKey)
                                {
                                    case ATR_FUNCTION:
                                        if ((merges & mergeFunction) == mergeFunction)
                                            setItemValue(key, ATTRIBUTES.ATTRIB_FUNCTION,
                                                dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty);
                                        break;

                                    case ATR_FOOD:
                                        if ((merges & mergeFood) == mergeFood)
                                            setItemValue(key, ATTRIBUTES.ATTRIB_FOOD,
                                                dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty);
                                        break;

                                    case ATR_SIDEFX:
                                        if ((merges & mergeSideFx) == mergeSideFx)
                                            setItemValue(key, ATTRIBUTES.ATTRIB_SIDEFX,
                                                dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty);
                                        break;

                                    case ATR_DETAILS:
                                        if ((merges & mergeDetails) == mergeDetails)
                                            setItemValue(key, ATTRIBUTES.ATTRIB_DETAILS,
                                                dataItem.FirstChild != null ? dataItem.FirstChild.Value : string.Empty);
                                        break;
                                }
                                System.Diagnostics.Debug.WriteLine("Read Data item=" + dataKey);
                            }
                        }
                    }
                }
            } // end for


        }
	}
}
