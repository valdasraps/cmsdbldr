#!/usr/bin/env python2.6

from suds.client import Client

class EGroup:

  url = 'https://foundservices.cern.ch/ws/egroups/v1/EgroupsWebService/EgroupsWebService.wsdl'

  def __init__(self, username, password):
    self.username = username
    self.password = password

  def members(self, groupname):
    client = Client(self.url, username = self.username, password = self.password)
    group = client.service.FindEgroupByName(groupname).result
    members = []
    for m in group.Members:
      if 'PrimaryAccount' in m:
        members.append(m.PrimaryAccount)
    return members

if __name__ == "__main__":
  print "CERN eGroup utility"
