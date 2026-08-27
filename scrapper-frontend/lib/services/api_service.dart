import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/linkedin_profile.dart';

class ApiService {
  final String baseUrl;

  const ApiService({this.baseUrl = 'http://localhost:8080'});

  Future<HybridLinkedInProfileResponse> scrapeProfile(String profileUrl) async {
    final uri = Uri.parse('$baseUrl/api/linkedin?type=hybrid');
    final response = await http.post(
      uri,
      headers: const {'Content-Type': 'application/json'},
      body: jsonEncode({'url': profileUrl}),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to fetch profile (${response.statusCode})');
    }

    final body = jsonDecode(response.body) as Map<String, dynamic>;
    return HybridLinkedInProfileResponse.fromJson(body);
  }
}
