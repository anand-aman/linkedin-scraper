import 'package:flutter/material.dart';

import '../models/linkedin_profile.dart';

class CurrentPositionCard extends StatelessWidget {
  final HybridCurrentPosition? currentPosition;

  const CurrentPositionCard({super.key, required this.currentPosition});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Current Position',
                style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            _field('Job Title', currentPosition?.jobTitle),
            _field('Company Name', currentPosition?.companyName),
            _field('Company LinkedIn URL', currentPosition?.companyLinkedinUrl),
            _field('Company Logo URL', currentPosition?.companyLogoUrl),
          ],
        ),
      ),
    );
  }

  Widget _field(String label, String? value) {
    final displayValue = value ?? 'null';
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: RichText(
        text: TextSpan(
          style: const TextStyle(color: Colors.black87),
          children: [
            TextSpan(
              text: '$label: ',
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            TextSpan(text: displayValue),
          ],
        ),
      ),
    );
  }
}
