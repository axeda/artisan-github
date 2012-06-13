import com.axeda.drm.sdk.Context
import com.axeda.drm.sdk.data.DataValue
import com.axeda.drm.sdk.data.DataValueList
import com.axeda.drm.sdk.data.HistoricalDataFinder
import com.axeda.drm.sdk.device.DataItem
import com.axeda.drm.sdk.device.DataItemFinder
import com.axeda.drm.sdk.device.Device
import com.axeda.drm.sdk.device.DeviceFinder
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver

/*
  Copyright 2011, Axeda Corporation

  GetChartData.groovy

  This is a Scripto custom object which uses the Axeda SDK to query for an asset with
  serial number 'asset1', then find historical data values for a data item named 'value1'.

  It finally packages the result as a JSON string and returns.
 */

Context context = Context.create()
if (parameters.username != null) context = Context.create(parameters.username)
DeviceFinder df = new DeviceFinder(context)

def serial = "asset1"
def diname = "value1"

df.setSerialNumber(serial)
Device device = df.find()

DataItemFinder dif = new DataItemFinder(context)
dif.model = device.model
dif.setDataItemName(diname)
DataItem dataItem = dif.find()

HistoricalDataFinder hdf = new HistoricalDataFinder(context, device)
DataValueList dvlist = hdf.find()

// use the xstream API to serialize the list to a JSON string
XStream xstream = new XStream(new JsonHierarchicalStreamDriver())
xstream.setMode(XStream.NO_REFERENCES);
String jsonstring = xstream.toXML(dvlist.list.collect {
  DataValue dv -> if (dv.dataItem.name==diname) [timestamp: dv.timestamp, value: dv.asString()]
})


return ['Content-Type': 'application/json', 'Content': jsonstring]
