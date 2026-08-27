import 'package:flutter/material.dart';

import '../models/linkedin_profile.dart';

class ExperienceSection extends StatelessWidget {
  final List<HybridExperience> experiences;

  const ExperienceSection({super.key, required this.experiences});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Experience', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            if (experiences.isEmpty)
              const Text('null')
            else
              ...experiences.asMap().entries.map(
                    (entry) => Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Item ${entry.key + 1}',
                            style: Theme.of(context).textTheme.bodyLarge,
                          ),
                          const SizedBox(height: 4),
                          _field('Job Title', entry.value.jobTitle),
                          _field('Company Name', entry.value.companyName),
                          _field('Description', entry.value.description),
                          _field('Location', entry.value.location),
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
