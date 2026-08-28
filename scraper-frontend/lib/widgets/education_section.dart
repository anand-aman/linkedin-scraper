import 'package:flutter/material.dart';

import '../models/linkedin_profile.dart';

class EducationSection extends StatelessWidget {
  final List<HybridEducation> education;

  const EducationSection({super.key, required this.education});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Education', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            if (education.isEmpty)
              const Text('null')
            else
              ...education.asMap().entries.map(
                    (entry) => Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Record ${entry.key + 1}',
                            style: Theme.of(context).textTheme.bodyLarge,
                          ),
                          const SizedBox(height: 4),
                          _field('Institution', entry.value.institution),
                          _field('Institution URL', entry.value.institutionUrl),
                          _field('Start Date', entry.value.startDate),
                          _field('End Date', entry.value.endDate),
                        ],
                      ),
                    ),
                  ),
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
